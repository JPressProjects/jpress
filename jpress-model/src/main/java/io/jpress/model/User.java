package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jboot.utils.StringUtils;
import io.jpress.model.base.BaseUser;


@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {

    public static final String STATUS_LOCK = "lock";    // 锁定账号，无法做任何事情
    public static final String STATUS_REG = "registed";         // 注册、未激活
    public static final String STATUS_OK = "ok";          // 正常、已激活

    /**
     * 默认头像地址
     */
    private static final String DEFAULT_AVATAR = "/static/admin/images/avatar.png";

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
        return StringUtils.isNotBlank(avatar) ? avatar : DEFAULT_AVATAR;
    }

    public void keepSafe() {
        remove("password", "salt");
    }
}
