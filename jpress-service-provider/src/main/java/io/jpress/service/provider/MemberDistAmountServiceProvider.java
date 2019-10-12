package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MemberDistAmountService;
import io.jpress.model.MemberDistAmount;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberDistAmountServiceProvider extends JbootServiceBase<MemberDistAmount> implements MemberDistAmountService {

}