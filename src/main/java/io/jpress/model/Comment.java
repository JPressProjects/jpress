package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseComment;

@Table(tableName = "comment", primaryKey = "id")
public class Comment extends BaseComment<Comment> {
	
}
