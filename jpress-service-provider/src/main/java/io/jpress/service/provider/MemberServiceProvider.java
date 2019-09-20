package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Member;
import io.jpress.service.MemberService;

@Bean
public class MemberServiceProvider extends JbootServiceBase<Member> implements MemberService {

}