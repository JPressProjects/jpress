package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.SiteInfoService;
import io.jpress.model.SiteInfo;
import io.jboot.service.JbootServiceBase;

@Bean
public class SiteInfoServiceProvider extends JbootServiceBase<SiteInfo> implements SiteInfoService {

}