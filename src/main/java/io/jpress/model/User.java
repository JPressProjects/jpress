package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseUser;

@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {
	
}
