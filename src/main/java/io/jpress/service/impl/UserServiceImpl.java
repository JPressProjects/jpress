
package io.jpress.service.impl;

import com.jfinal.kit.Ret;
import io.jpress.service.UserService;
import io.jpress.model.User;
import io.jboot.db.service.JbootServiceBase;

public class UserServiceImpl extends JbootServiceBase<User> implements UserService {

    @Override
    public Ret doLogin(String loginName, String password) {
        return null;
    }
}
