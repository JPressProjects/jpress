package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.AttachmentVideoService;
import io.jpress.model.AttachmentVideo;
import io.jboot.service.JbootServiceBase;

@Bean
public class AttachmentVideoServiceProvider extends JbootServiceBase<AttachmentVideo> implements AttachmentVideoService {

}