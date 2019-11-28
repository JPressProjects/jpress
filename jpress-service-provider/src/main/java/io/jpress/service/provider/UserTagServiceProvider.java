package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserTagService;
import io.jpress.model.UserTag;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserTagServiceProvider extends JbootServiceBase<UserTag> implements UserTagService {

}