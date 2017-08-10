package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseMetadata;

@Table(tableName = "metadata", primaryKey = "id")
public class Metadata extends BaseMetadata<Metadata> {
	
}
