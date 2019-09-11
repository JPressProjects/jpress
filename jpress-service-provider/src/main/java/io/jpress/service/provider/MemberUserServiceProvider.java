package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MemberUserService;
import io.jpress.model.MemberUser;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberUserServiceProvider extends JbootServiceBase<MemberUser> implements MemberUserService {

}