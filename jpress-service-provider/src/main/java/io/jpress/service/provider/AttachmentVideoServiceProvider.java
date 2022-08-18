package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.AttachmentVideo;
import io.jpress.service.AttachmentVideoService;

@Bean
public class AttachmentVideoServiceProvider extends JPressServiceBase<AttachmentVideo> implements AttachmentVideoService {

    @Override
    public AttachmentVideo findByUuid(String uuid) {
        return DAO.findFirstByColumn("uuid",uuid);
    }
}