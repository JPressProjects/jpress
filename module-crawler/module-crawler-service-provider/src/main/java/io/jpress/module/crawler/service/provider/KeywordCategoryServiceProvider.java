package io.jpress.module.crawler.service.provider;

import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
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

    public Page<KeywordCategory> paginate(int pageNum, int pageSize, String name) {

        Columns columns = Columns.create();
        if (StrUtil.notBlank(name)) {
            columns.likeAppendPercent("name", name);
        }

        return DAO.paginateByColumns(pageNum, pageSize, columns.getList(), "id desc");
    }

    @Override
    public KeywordCategory findByName(String name) {
        return DAO.findFirstByColumn("name", name);
    }

    public boolean batchSave(List<String> categoryList) {

        List<KeywordCategory> list = Lists.newArrayList();
        categoryList = categoryList.stream().distinct().collect(Collectors.toList());
        final Date created = new Date();

        categoryList.stream().forEach(name -> {
            KeywordCategory category = new KeywordCategory();
            category.setName(name);
            category.setCreated(created);
            list.add(category);
        });

        int[] totalNum = Db.batchSave(list, list.size());
        if (totalNum.length == categoryList.size()) {
            return true;
        }
        return false;
    }

    @Override
    public Object save(KeywordCategory category) {
        Object id = super.save(category);
        return id;
    }

    public boolean delete(Object id) {
        boolean deleted = super.deleteById(id);

        if (deleted) {
            deleted = keywordService.deleteByCategoryId(id);
        }

        // TODO 缓存处理

        return deleted;
    }



}