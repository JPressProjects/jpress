package io.jpress.install;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.utils.HashUtils;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;

@UrlMapping(url = "/install")
@Before(InstallInterceptor.class)
public class InstallController extends JBaseController {

	public void index() {
		render("/WEB-INF/install/step1.html");
	}

	public void step2() {
		String db_host = getPara("db_host");
		String db_name = getPara("db_name");
		String db_user = getPara("db_user");
		String db_password = getPara("db_password");
		String db_table_prefix = getPara("db_tablePrefix");
		
		
		if(!StrKit.notBlank(db_host,db_name,db_user)){
			render("/WEB-INF/install/step2.html");
			return;
		}
		
		InstallUtils.init(db_host, db_name, db_user, db_password, db_table_prefix);
		
		try {
			List<String> tableList = InstallUtils.getTableList();
			if(null != tableList && tableList.size() > 0){
				if( tableList.contains(db_table_prefix+"attachment") ||
					tableList.contains(db_table_prefix+"comment") ||
					tableList.contains(db_table_prefix+"content") ||
					tableList.contains(db_table_prefix+"mapping") ||
					tableList.contains(db_table_prefix+"metadata") ||
					tableList.contains(db_table_prefix+"option") ||
					tableList.contains(db_table_prefix+"taxonomy") ||
					tableList.contains(db_table_prefix+"user"))
				{ //table has contains
					redirect("/install/step2_error");
					return;
				}
			}
		} catch (Exception e) { //db config error
			
			redirect("/install/step2_error");
			return;
		}
		
		try {
			
			InstallUtils.createJpressDatabase();
			
			List<String> tableList = InstallUtils.getTableList();
			if(null != tableList && tableList.size() > 0){
				if( tableList.contains(db_table_prefix+"attachment") &&
					tableList.contains(db_table_prefix+"comment") &&
					tableList.contains(db_table_prefix+"content") &&
					tableList.contains(db_table_prefix+"mapping") &&
					tableList.contains(db_table_prefix+"metadata") &&
					tableList.contains(db_table_prefix+"option") &&
					tableList.contains(db_table_prefix+"taxonomy") &&
					tableList.contains(db_table_prefix+"user"))
				{ 
					//createJpressDatabase success
//					keepParaToCache();
					redirect("/install/step3");
					return;
				}
			}
			
		} catch (Exception e) {e.printStackTrace();}
		
		
		redirect("/install/step2_error");
	}



	public void step2_error() {

		render("/WEB-INF/install/step2_error.html");
	}

	public void step3() throws SQLException {
		
		String webname = getPara("webname");
		String username = getPara("username");
		String password = getPara("password");
		
		if(StrKit.isBlank(webname) || StrKit.isBlank(username) || StrKit.isBlank(password)){
			render("/WEB-INF/install/step3.html");
			return;
		}
		
		InstallUtils.setWebName(webname);	
		
		String salt = HashUtils.salt();
		password = HashUtils.md5WithSalt(password,salt);
		InstallUtils.setWebFirstUser(username,password,salt);
		
		InstallUtils.createDbProperties();
		InstallUtils.createJpressProperties();
		
		redirect("/");
	}
	
	
	
}
