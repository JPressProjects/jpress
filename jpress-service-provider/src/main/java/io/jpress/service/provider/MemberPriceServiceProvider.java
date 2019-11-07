package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MemberPriceService;
import io.jpress.model.MemberPrice;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberPriceServiceProvider extends JbootServiceBase<MemberPrice> implements MemberPriceService {

}