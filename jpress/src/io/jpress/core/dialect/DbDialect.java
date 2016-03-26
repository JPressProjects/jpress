package io.jpress.core.dialect;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.plugin.druid.DruidPlugin;

public abstract class DbDialect {

	// key:classSimpleName.toLowerCase value:tableName
	protected static Map<String, String> tableMapping = new HashMap<String, String>();
	public static void mapping(String key, String value) {
		tableMapping.put(key, value);
	}

	public abstract String forShowTable();
	public abstract String forInstall(String tablePrefix);
	public abstract String forSelect(String tableName);
	public abstract String forDelete(String tableName);
	public abstract String forSelectCount(String tableName);
	public abstract String forPaginateFrom(String tableName,String where);
	public abstract String forInsertWebName(String tablePrefix);
	public abstract String forInsertFirstUser(String tablePrefix);
	
	/**
	 * becuse the table name is uncertainty,
	 * invoke this method convert sql to correct.
	 * @param sql
	 * @return
	 */
	public abstract String doTableConvert(String sql);

	public abstract DruidPlugin createDuidPlugin(String dbHost, String dbName,String dbUser, String dbPassword);

}
