package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.TemplateBlockInfoService;
import io.jpress.model.TemplateBlockInfo;
import io.jboot.service.JbootServiceBase;

@Bean
public class TemplateBlockInfoServiceProvider extends JbootServiceBase<TemplateBlockInfo> implements TemplateBlockInfoService {

}