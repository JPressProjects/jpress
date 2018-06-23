package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseUser;


@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {

    public static final int STATUS_LOCK_ID = -1;    // 锁定账号，无法做任何事情
    public static final int STATUS_REG = 0;         // 注册、未激活
    public static final int STATUS_OK = 1;          // 正常、已激活

    public boolean isStatusOk() {
        return getStatus() == STATUS_OK;
    }

    public boolean isStatusReg() {
        return getStatus() == STATUS_REG;
    }

    public boolean isStatusLocked() {
        return getStatus() == STATUS_LOCK_ID;
    }

}
