package io.jpress.module.form.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

@Bean
public class FormInfoServiceProvider extends JPressServiceBase<FormInfo> implements FormInfoService {

}