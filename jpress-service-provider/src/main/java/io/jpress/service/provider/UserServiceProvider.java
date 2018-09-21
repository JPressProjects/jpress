package io.jpress.service.provider;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Singleton;

@Bean
@Singleton
public class UserServiceProvider extends JbootServiceBase<User> implements UserService {

    @Override
    public Page<User> paginate(int page, int pagesize) {
        return DAO.paginate(page, pagesize);
    }

    @Override
    public Ret loginByUsername(String username, String pwd) {
        User user = DAO.findFirstByColumn("username", username.trim().toLowerCase());
        return doValidateUserPwd(user, pwd);
    }

    @Override
    public Ret loginByEmail(String email, String pwd) {
        User user = DAO.findFirstByColumn("email", email.trim().toLowerCase());
        return doValidateUserPwd(user, pwd);
    }


    public Ret doValidateUserPwd(User user, String pwd) {

        if (user == null) {
            return Ret.fail("msg", "用户名或密码不正确");
        }

        if (user.isStatusLocked()) {
            return Ret.fail("msg", "该账号已被冻结");
        }

        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + pwd);

        // 未通过密码验证
        if (user.getPassword().equals(hashedPass) == false) {
            return Ret.fail("msg", "用户名或密码不正确");
        }

        return Ret.ok().set("user", user);
    }
    
}