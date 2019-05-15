package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.DictType;
import io.jpress.service.DictTypeService;

@Bean
public class DictTypeServiceProvider extends JbootServiceBase<DictType> implements DictTypeService {

}