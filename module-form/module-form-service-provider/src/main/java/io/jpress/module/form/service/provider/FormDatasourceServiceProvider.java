package io.jpress.module.form.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.form.service.FormDatasourceService;
import io.jpress.module.form.model.FormDatasource;
import io.jpress.commons.service.JPressServiceBase;

@Bean
public class FormDatasourceServiceProvider extends JPressServiceBase<FormDatasource> implements FormDatasourceService {

}