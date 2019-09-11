package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.PaymentRecordService;
import io.jpress.module.shop.model.PaymentRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class PaymentRecordServiceProvider extends JbootServiceBase<PaymentRecord> implements PaymentRecordService {

}