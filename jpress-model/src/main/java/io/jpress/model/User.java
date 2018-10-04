package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jboot.utils.StrUtils;
import io.jpress.model.base.BaseUser;


@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {

    public static final String STATUS_LOCK = "locked";    // 锁定账号，无法做任何事情
    public static final String STATUS_REG = "registered";         // 注册、未激活
    public static final String STATUS_OK = "ok";          // 正常、已激活

    /**
     * 默认头像地址
     */
    private static final String DEFAULT_AVATAR = "/static/commons/img/avatar.png";

    public boolean isStatusOk() {
        return STATUS_OK.equals(getStatus());
    }

    public boolean isStatusReg() {
        return STATUS_REG.equals(getStatus());
    }

    public boolean isStatusLocked() {
        return STATUS_LOCK.equals(getStatus());
    }


    @Override
    public String getAvatar() {
        String avatar = super.getAvatar();
        return StrUtils.isNotBlank(avatar) ? avatar : DEFAULT_AVATAR;
    }

    public void keepSafe() {
        remove("password", "salt");
    }

    public void keepUpdateSafe() {

        //这些字段不允许用户自己更新
        
        remove("password", "salt",
                "username", "wx_openid",
                "wx_unionid", "qq_openid",
                "email_status", "mobile_status",
                "status", "created",
                "create_source", "logged",
                "activated");

    }


    public boolean isEmailStatusOk() {
        return STATUS_OK.equals(getEmailStatus());
    }


    public boolean isMobileStatusOk() {
        return STATUS_OK.equals(getMobileStatus());
    }

    public String getDetailUrl() {
        return "/admin/user/detail/" + getId();
    }
}
