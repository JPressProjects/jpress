package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ModelUtil;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

@Bean
public class SinglePageCategoryServiceProvider extends JbootServiceBase<SinglePageCategory> implements SinglePageCategoryService {

    @Override
    public List<SinglePageCategory> findListByType(String type) {
        return ModelUtil.copy(findListByTypeWithCache(type));
    }

    @Override
    public SinglePageCategory findFirstByTypeAndSlug(String type, String slug) {
        return DAO.findFirstByColumns(Columns.create("type", type).eq("slug", slug));
    }

    @Override
    public void doUpdatePageCount(Long categoryId) {
        long articleCount = Db.queryLong("select count(*) from single_page_category_mapping where category_id = ? ", categoryId);
        SinglePageCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(articleCount);
            update(category);
        }
    }

    @Override
    public Long[] findCategoryIdsBySinglePageId(Long singlePageId) {
        List<Record> records = Db.find("select * from single_page_category_mapping where single_page_id = ?", singlePageId);
        if (records == null || records.isEmpty()) {
            return null;
        }

        return ArrayUtils.toObject(records.stream().mapToLong(record -> record.get("category_id")).toArray());
    }

    //    @Cacheable(name = "singlePageCategory", key = "type:#(type)")
    public List<SinglePageCategory> findListByTypeWithCache(String type) {
        return DAO.findListByColumns(Columns.create("type", type), "order_number asc,id desc");
    }
}