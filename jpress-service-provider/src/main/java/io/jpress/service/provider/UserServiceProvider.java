/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.JbootDb;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.model.User;
import io.jpress.model.UserOpenid;
import io.jpress.service.UserOpenidService;
import io.jpress.service.UserService;

import java.math.BigDecimal;
import java.util.Date;

@Bean
public class UserServiceProvider extends JbootServiceBase<User> implements UserService {

    @Inject
    private UserOpenidService openidService;

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            User user = findById(id);
            if (user != null) {
                delete(user); //必须通过  delete(user) 才能清除缓存
            }
        }
        return true;
    }


    @Override
    public Page<User> _paginate(int page, int pagesize, Columns columns, Long memberGroupId) {
        if (memberGroupId == null) {
            return DAO.paginateByColumns(page, pagesize, columns, "id desc");
        }

        StringBuilder sqlBuilder = new StringBuilder("from `user` u ");
        sqlBuilder.append(" left join member m on u.id = m.user_id ");

        columns.add("m.group_id", memberGroupId);
        sqlBuilder.append(SqlUtils.toWhereSql(columns));
        sqlBuilder.append(" order by u.id desc");

        return DAO.paginate(page, pagesize, "select u.* , m.group_id ", sqlBuilder.toString(), columns.getValueArray());
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) {
        return StrUtil.isEmail(usernameOrEmail)
                ? DAO.findFirstByColumn("email", usernameOrEmail.trim().toLowerCase())
                : DAO.findFirstByColumn("username", usernameOrEmail.trim().toLowerCase());
    }


    @Override
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

        // 更新用户的登录时间
        updateUserLoginedDate(user);

        return Ret.ok().set("user_id", user.getId());
    }


    private void updateUserLoginedDate(User user) {
        user.setLogged(new Date());
        update(user);
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from user where status = ?", status);
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        User user = findById(id);
        user.setStatus(status);
        return update(user);
    }

    @Override
    public User findFistByUsername(String username) {
        return DAO.findFirstByColumn("username", username);
    }

    @Override
    public User findFistByEmail(String email) {
        return DAO.findFirstByColumn("email", email);
    }

    @Override
    public User findFistByWxUnionid(String unioinId) {
//        return DAO.findFirstByColumn("wx_unionid", unioinId);
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_WECHAT_UNIONID, unioinId);
    }

    @Override
//    @Cacheable(name = "userOpenIds", key = "#(openId)")
    public User findFistByWxOpenid(String openId) {
//        return DAO.findFirstByColumn("wx_openid", openId);
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_WECHAT, openId);
    }

    @Override
    public User findFistByQQOpenid(String openId) {
//        return DAO.findFirstByColumn("qq_openid", openId);
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_QQ, openId);
    }

    @Override
    public User findFistByWeiboOpenid(String openId) {
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_WEIBO, openId);
    }

    @Override
    public User findFistByGithubOpenid(String openId) {
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_GITHUB, openId);
    }

    @Override
    public User findFistByGiteeOpenid(String openId) {
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_GITEE, openId);
    }

    @Override
    public User findFistByDingdingOpenid(String openId) {
        return openidService.findByTypeAndOpenId(UserOpenid.TYPE_DINGDING, openId);
    }

    @Override
    public BigDecimal queryUserAmount(Object userId) {
        BigDecimal value = JbootDb.queryBigDecimal("select amount from user_amount where user_id = ?", userId);
        return value == null ? BigDecimal.ZERO : value;
    }

    @Override
    public boolean updateUserAmount(Object userId, BigDecimal oldAmount, BigDecimal updateAmount) {
        BigDecimal value = JbootDb.queryBigDecimal("select amount from user_amount where user_id = ?", userId);
        if (value == null) {
            Record record = new Record();
            record.set("user_id", userId);
            record.set("amount", updateAmount);
            record.set("modified", new Date());
            record.set("created", new Date());
            return Db.save("user_amount", record);
        } else {
            return Db.update("update user_amount set amount = amount + " + updateAmount.longValue()
                    + " where user_id = ? and amount = ?", userId, oldAmount) > 0;
        }
    }


    @Override
    public void shouldUpdateCache(int action, Object data) {
        if (data instanceof User) {
            User user = (User) data;
//            if (user.getWxOpenid() != null) {
//                Jboot.getCache().remove("userOpenIds", user.getWxOpenid());
//            }
        }
    }

    private static final String[] defaultJoinAttrs = new String[]{"id", "username", "nickname", "avatar", "created", "signature"};

    @Override
    public <M extends Model> M join(M model, String joinOnField) {
        return super.join(model, joinOnField, defaultJoinAttrs);
    }
}