package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.CouponCodeService;
import io.jpress.module.shop.model.CouponCode;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponCodeServiceProvider extends JbootServiceBase<CouponCode> implements CouponCodeService {

}