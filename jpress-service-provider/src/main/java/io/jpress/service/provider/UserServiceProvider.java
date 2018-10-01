package io.jpress.service.provider;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.model.User;
import io.jpress.service.UserService;

import javax.inject.Singleton;

@Bean
@Singleton
public class UserServiceProvider extends JbootServiceBase<User> implements UserService {

    @Override
    public boolean deleteByIds(Object... ids) {
        return Db.update("delete from user where id in  " + SqlUtils.buildInSqlPara(ids)) > 0;
    }


    @Override
    public Page<User> _paginate(int page, int pagesize, String username, String email, String status) {

        Columns columns = Columns.create("status", status);

        SqlUtils.likeAppend(columns, "username", username);
        SqlUtils.likeAppend(columns, "email", email);

        return DAO.paginateByColumns(page, pagesize, columns, "id desc");
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
            return Ret.fail("message", "用户名或密码不正确");
        }

        if (user.isStatusLocked()) {
            return Ret.fail("message", "该账号已被冻结");
        }

        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + pwd);

        // 未通过密码验证
        if (user.getPassword().equals(hashedPass) == false) {
            return Ret.fail("message", "用户名或密码不正确");
        }

        return Ret.ok().set("user_id", user.getId());
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from user where status = ?", status);
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        User user = findById(id);
        user.setStatus(status);
        return user.update();
    }

    @Override
    public User findFistByUsername(String username) {
        return DAO.findFirstByColumn("username", username);
    }

    @Override
    public User findFistByEmail(String email) {
        return DAO.findFirstByColumn("email", email);
    }


}