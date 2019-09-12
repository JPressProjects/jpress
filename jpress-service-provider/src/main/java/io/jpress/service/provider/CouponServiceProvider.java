package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.CouponService;
import io.jpress.model.Coupon;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponServiceProvider extends JbootServiceBase<Coupon> implements CouponService {

}