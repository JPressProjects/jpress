package io.jpress.service.impl;

import com.jfinal.kit.Ret;
import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserService;

/**
 * Created by michael on 2017/8/10.
 */
@Bean
public class UserServiceImpl implements UserService {
    @Override
    public Ret login(String username, String password) {
        return Ret.fail();
    }
}
