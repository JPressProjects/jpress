package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MemberService;
import io.jpress.model.Member;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberServiceProvider extends JbootServiceBase<Member> implements MemberService {

}