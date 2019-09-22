package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.MemberGroupService;
import io.jpress.model.MemberGroup;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberGroupServiceProvider extends JbootServiceBase<MemberGroup> implements MemberGroupService {

}