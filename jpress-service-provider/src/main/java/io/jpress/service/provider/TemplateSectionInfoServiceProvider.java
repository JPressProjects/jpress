package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.TemplateSectionInfoService;
import io.jpress.model.TemplateSectionInfo;
import io.jboot.service.JbootServiceBase;

@Bean
public class TemplateSectionInfoServiceProvider extends JbootServiceBase<TemplateSectionInfo> implements TemplateSectionInfoService {

}