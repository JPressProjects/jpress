package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseCategory;

@Table(tableName = "category", primaryKey = "id")
public class Category extends BaseCategory<Category> {
	
}
