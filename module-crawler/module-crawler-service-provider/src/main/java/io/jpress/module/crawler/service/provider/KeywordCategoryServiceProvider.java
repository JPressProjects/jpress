package io.jpress.module.crawler.service.provider;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jpress.module.crawler.model.Keyword;
import io.jpress.module.crawler.service.KeywordCategoryService;
import io.jpress.module.crawler.model.KeywordCategory;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.crawler.service.KeywordService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class KeywordCategoryServiceProvider extends JbootServiceBase<KeywordCategory> implements KeywordCategoryService {

    @Inject
    KeywordService keywordService;

    @Override
    public Page<KeywordCategory> paginate(int pageNum, int pageSize, String name) {

        Columns columns = Columns.create();
        if (StrUtil.notBlank(name)) {
            columns.likeAppendPercent("name", name);
        }

        return DAO.paginateByColumns(pageNum, pageSize, columns.getList(), "order_list desc");
    }

    @Override
    @Cacheable(name = KeywordCategory.CACHE_NAME, key = "#(name)")
    public KeywordCategory findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    @CacheEvict(name = KeywordCategory.CACHE_NAME)
    public boolean batchSave(List<String> categoryList) {

        List<KeywordCategory> list = Lists.newArrayList();
        categoryList = categoryList.stream().distinct().collect(Collectors.toList());
        final Date created = new Date();

        categoryList.stream().forEach(name -> {
            KeywordCategory category = new KeywordCategory();
            category.setName(name);
            category.setCreated(created);

            List<Pinyin> pinyinList = HanLP.convertToPinyinList(name);
            String head = pinyinList.get(0).getHeadString();
            String pinyin = "none".equals(head) ? "-" : head;
            category.setCode(pinyin);

            list.add(category);
        });

        int[] totalNum = Db.batchSave(list, list.size());
        if (totalNum.length == categoryList.size()) {
            return true;
        }
        return false;
    }

    @Override
    @CacheEvict(name = KeywordCategory.CACHE_NAME)
    public Object save(KeywordCategory category) {
        Object id = super.save(category);
        return id;
    }

    @CacheEvict(name = KeywordCategory.CACHE_NAME, key = "#(id)")
    public boolean delete(Object id) {
        boolean deleted = super.deleteById(id);

        if (deleted) {
            deleted = keywordService.deleteByCategoryId(id);
        }

        /** 删除关键词缓存 */
        deleteCacheById(id);
        return deleted;
    }

    @Override
    public void deleteCacheById(Object id) {
        Jboot.getCache().remove(KeywordCategory.CACHE_NAME, id);
    }

    @Override
    @CacheEvict(name = KeywordCategory.CACHE_NAME)
    public boolean countAll() {
        List<Record> list = keywordService.findAllCountByCategoryId();
        List<KeywordCategory> categoryList = Lists.newArrayList();
        Date modified = new Date();

        list.stream().forEach(obj -> {
            KeywordCategory category = findById(obj.get("category_id"));
            category.setTotalNum(obj.getInt("total_num"));
            category.setModified(modified);
            categoryList.add(category);
        });

        int[] result = Db.batchUpdate(categoryList, categoryList.size());
        return result.length == categoryList.size() ? true : false;
    }

    @Override
    @CacheEvict(name = KeywordCategory.CACHE_NAME, key = "#(id)")
    public boolean countById(Object id) {

        Record record = keywordService.findCountByCategoryId(id);

        if (record == null) {
            return true;
        }

        KeywordCategory category = findById(record.get("category_id"));
        category.setTotalNum(record.getInt("total_num"));
        category.setModified(new Date());

        return category.saveOrUpdate();
    }

}