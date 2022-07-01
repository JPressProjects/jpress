package io.jpress.module.form.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.module.form.model.FormInfo;
import io.jboot.service.JbootServiceBase;

@Bean
public class FormInfoServiceProvider extends JbootServiceBase<FormInfo> implements FormInfoService {

}