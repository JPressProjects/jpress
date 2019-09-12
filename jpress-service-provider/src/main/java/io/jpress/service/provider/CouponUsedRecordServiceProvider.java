package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.CouponUsedRecordService;
import io.jpress.model.CouponUsedRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponUsedRecordServiceProvider extends JbootServiceBase<CouponUsedRecord> implements CouponUsedRecordService {

}