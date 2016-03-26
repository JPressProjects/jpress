package io.jpress.model;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseComment;

import com.jfinal.plugin.activerecord.Page;

@Table(tableName = "comment", primaryKey = "id")
public class Comment extends BaseComment<Comment> {
	private static final long serialVersionUID = 1L;

	public static final Comment DAO = new Comment();

	public Page<Comment> doPaginate(int pageNumber, int pageSize,
			String module, String type) {
		
		String select = " select c.*,content.title content_title,u.username";
		String sqlExceptSelect = " from comment c "
				+ "left join content on c.content_id = content.id "
				+ "left join `user` u on c.user_id = u.id "
				+ "order by c.created";
		
		return paginate(pageNumber, pageSize, select, sqlExceptSelect);
		
	}
	
	
	public String getUsername(){
		return get("username");
	}
	
	public String getcontentTitle(){
		return get("content_title");
	}
}
