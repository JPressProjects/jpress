package io.jpress.model;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseUser;

@Table(tableName="user",primaryKey="id")
public class User extends BaseUser<User> {

	private static final long serialVersionUID = 1L;
	public static final User DAO = new User();
	
	public static String ROLE_ADMINISTRATOR = "administrator";
	
	
	public static User findUserByContentId(Long contentId){
		return DAO.doFindFirst( "content_id = ?",contentId);
	}
	
	public static User findUserById(long userId){
		return DAO.findById(userId);
	}
	
	public static User findUserByEmail(String email){
		return DAO.doFindFirst("email = ?",email);
	}
	
	public static User findUserByUsername(String username){
		return DAO.doFindFirst("username = ?",username);
	}
	
	public boolean isAdministrator(){
		return  "administrator".equals(getRole());
	}

}
