package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.AttachmentVideoCategoryService;
import io.jpress.model.AttachmentVideoCategory;
import io.jboot.service.JbootServiceBase;

@Bean
public class AttachmentVideoCategoryServiceProvider extends JbootServiceBase<AttachmentVideoCategory> implements AttachmentVideoCategoryService {

}