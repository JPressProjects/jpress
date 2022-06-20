package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.TemplateSectionConfigService;
import io.jpress.model.TemplateSectionConfig;
import io.jboot.service.JbootServiceBase;

@Bean
public class TemplateSectionConfigServiceProvider extends JbootServiceBase<TemplateSectionConfig> implements TemplateSectionConfigService {

}