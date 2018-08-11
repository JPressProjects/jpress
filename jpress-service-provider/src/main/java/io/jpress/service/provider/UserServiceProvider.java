package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserService;
import io.jpress.model.User;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class UserServiceProvider extends JbootServiceBase<User> implements UserService {

    @Override
    public Page<User> paginate(int page, int pagesize) {
        return DAO.paginate(page, pagesize);
    }
}