package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseAttachment;

@Table(tableName = "attachment", primaryKey = "id")
public class Attachment extends BaseAttachment<Attachment> {
	
}
