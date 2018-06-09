package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.PaymentRecordService;
import io.jpress.model.PaymentRecord;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class PaymentRecordServiceProvider extends JbootServiceBase<PaymentRecord> implements PaymentRecordService {

}