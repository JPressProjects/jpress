package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;

@Bean
public class SinglePageCategoryServiceProvider extends JPressServiceBase<SinglePageCategory> implements SinglePageCategoryService {


    @Override
    public void doUpdatePageCategoryCount(Long categoryId) {
        Long count = Db.queryLong("select count(*) from single_page where category_id = ? ", categoryId);
        SinglePageCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(count);
            update(category);
        }
    }

}