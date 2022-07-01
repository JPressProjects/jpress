package io.jpress.module.form.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.form.service.FormDictService;
import io.jpress.module.form.model.FormDict;
import io.jboot.service.JbootServiceBase;

@Bean
public class FormDictServiceProvider extends JbootServiceBase<FormDict> implements FormDictService {

}