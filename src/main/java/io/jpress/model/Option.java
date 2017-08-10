package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseOption;

@Table(tableName = "option", primaryKey = "id")
public class Option extends BaseOption<Option> {
    public static final String KEY_WEB_NAME = "web_name";
    public static final String KEY_TEMPLATE_ID = "web_template_id";
}
