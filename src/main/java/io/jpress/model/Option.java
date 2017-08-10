package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseOption;

@Table(tableName = "option", primaryKey = "id")
public class Option extends BaseOption<Option> {
	
}
