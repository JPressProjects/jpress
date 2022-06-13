package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ModelUtil;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;

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
    public void doUpdatePageCategoryCount(Long categoryId) {
        long count = Db.queryLong("select count(*) from single_page where category_id = ? ", categoryId);
        SinglePageCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(count);
            update(category);
        }
    }

    //    @Cacheable(name = "singlePageCategory", key = "type:#(type)")
    public List<SinglePageCategory> findListByTypeWithCache(String type) {
        return DAO.findListByColumns(Columns.create("type", type), "order_number asc,id desc");
    }
}