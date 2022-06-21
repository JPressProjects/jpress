package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.TemplateBlockConfigService;
import io.jpress.model.TemplateBlockConfig;
import io.jboot.service.JbootServiceBase;

@Bean
public class TemplateBlockConfigServiceProvider extends JbootServiceBase<TemplateBlockConfig> implements TemplateBlockConfigService {

}