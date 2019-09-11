package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.CouponUsedRecordService;
import io.jpress.module.shop.model.CouponUsedRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponUsedRecordServiceProvider extends JbootServiceBase<CouponUsedRecord> implements CouponUsedRecordService {

}