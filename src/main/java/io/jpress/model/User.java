package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseUser;

@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {
    
    public static final String ROLE_ADMINISTRATOR = "administrator";

    public static final String STATUS_NORMAL = "normal";
    public static final String STATUS_FROZEN = "frozen";

    public boolean isAdministrator() {
        return ROLE_ADMINISTRATOR.equals(getRole());
    }
}
