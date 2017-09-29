
package io.jpress.service.impl;

import com.jfinal.kit.Ret;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.Cacheable;
import io.jpress.service.UserService;
import io.jpress.model.User;
import io.jboot.db.service.JbootServiceBase;
import io.jpress.utils.EncryptUtils;

@Bean
public class UserServiceImpl extends JbootServiceBase<User> implements UserService {


    /**
     * 进行后台登陆，只有超级管理员才能登陆
     *
     * @param loginName
     * @param password
     * @return
     */
    @Override
    public Ret doLogin(String loginName, String password) {
        User user = findByUserName(loginName);

        if (user == null) {
            return Ret.fail("msg", "没有该用户").set("code", 1);
        }

        if (!user.isAdministrator()) {
            return Ret.fail("msg", "您没有登陆权限").set("code", 2);
        }

        String uPwd = EncryptUtils.encryptPassword(password, user.getSalt());
        if (!uPwd.equals(user.getPassword())) {
            return Ret.fail("msg", "密码错误").set("code", 3);
        }

        return Ret.ok("user", user);
    }

    /**
     * 根据loginName 查询用户信息，会自动缓存，缓存的key为：loginName的值
     *
     * @param username
     * @return
     */
    @Cacheable(name = "user", key = "#(username)")
    @Override
    public User findByUserName(String username) {
        return DAO.findFirstByColumn("username", username);
    }

    /**
     * user 更新后，删除缓存信息
     *
     * @param user
     * @return
     */
    @CacheEvict(name = "user", key = "#(user.loginName)")
    @Override
    public boolean update(User user) {
        return super.update(user);
    }
}
