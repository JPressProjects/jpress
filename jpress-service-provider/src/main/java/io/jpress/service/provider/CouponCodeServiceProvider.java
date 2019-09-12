package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.CouponCodeService;
import io.jpress.model.CouponCode;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponCodeServiceProvider extends JbootServiceBase<CouponCode> implements CouponCodeService {

}