package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseContent;

@Table(tableName = "content", primaryKey = "id")
public class Content extends BaseContent<Content> {
	
}
