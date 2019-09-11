package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.CouponService;
import io.jpress.module.shop.model.Coupon;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponServiceProvider extends JbootServiceBase<Coupon> implements CouponService {

}