package io.jpress.module.route.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.route.service.TViewRecordService;
import io.jpress.module.route.model.TViewRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class TViewRecordServiceProvider extends JbootServiceBase<TViewRecord> implements TViewRecordService {

}