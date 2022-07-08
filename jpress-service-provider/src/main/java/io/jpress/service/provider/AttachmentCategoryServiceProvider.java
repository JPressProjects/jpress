package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.AttachmentCategory;
import io.jpress.service.AttachmentCategoryService;

@Bean
public class AttachmentCategoryServiceProvider extends JPressServiceBase<AttachmentCategory> implements AttachmentCategoryService {

    /**
     * 更新 model
     * @param categoryId
     */
    @Override
    public void doUpdateAttachmentCategoryCount(Long categoryId) {
        long count = Db.queryLong("select count(*) from attachment where category_id = ? ", categoryId);
        AttachmentCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(count);
            update(category);
        }
    }
}