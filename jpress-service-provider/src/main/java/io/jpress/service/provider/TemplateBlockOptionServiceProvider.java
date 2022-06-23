package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.TemplateBlockOptionService;
import io.jpress.model.TemplateBlockOption;
import io.jboot.service.JbootServiceBase;

@Bean
public class TemplateBlockOptionServiceProvider extends JbootServiceBase<TemplateBlockOption> implements TemplateBlockOptionService {

}