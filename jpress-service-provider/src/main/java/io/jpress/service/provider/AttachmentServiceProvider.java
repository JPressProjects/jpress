package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.service.AttachmentService;
import io.jpress.model.Attachment;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class AttachmentServiceProvider extends JbootServiceBase<Attachment> implements AttachmentService {

    @Override
    public Page paginate(int page, int pagesieze) {
        return DAO.paginate(page, pagesieze);
    }
}