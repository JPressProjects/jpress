package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtils;
import io.jpress.service.AttachmentService;
import io.jpress.model.Attachment;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class AttachmentServiceProvider extends JbootServiceBase<Attachment> implements AttachmentService {

    @Override
    public Page _paginate(int page, int pagesieze, String title) {
        Columns columns = Columns.create();
        if (StrUtils.isNotBlank(title)) {
            columns.like("title", "%" + title + "%");
        }

        return DAO.paginateByColumns(page, pagesieze, columns, "id desc");
    }
}