package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.AttachmentVideoCategory;
import io.jpress.service.AttachmentVideoCategoryService;

@Bean
public class AttachmentVideoCategoryServiceProvider extends JPressServiceBase<AttachmentVideoCategory> implements AttachmentVideoCategoryService {

    @Override
    public void doUpdateVideoCategoryCount(Long categoryId) {
        long count = Db.queryLong("select count(*) from attachment_video where category_id = ? ", categoryId);
        AttachmentVideoCategory category = findById(categoryId);
        if (category != null) {
            category.setCount(count);
            update(category);
        }
    }
}