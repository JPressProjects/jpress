package io.jpress.module.form.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.form.service.FormDictItemService;
import io.jpress.module.form.model.FormDictItem;
import io.jboot.service.JbootServiceBase;

@Bean
public class FormDictItemServiceProvider extends JbootServiceBase<FormDictItem> implements FormDictItemService {

}