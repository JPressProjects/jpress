/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.db;

import io.jpress.utils.FileUtils;

import java.io.File;
import java.util.Map;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.druid.DruidPlugin;

public class MysqlDialect extends DbDialect {

	@Override
	public String forShowTable() {
		return "show tables;";
	}

	@Override
	public String forSelect(String tableName) {
		return String.format("SELECT * FROM `%s`", tableName);
	}

	@Override
	public String forDelete(String tableName) {
		return String.format("DELETE FROM `%s`", tableName);
	}

	@Override
	public String forSelectCount(String tableName) {
		return String.format("SELECT COUNT(*) FROM `%s`", tableName);
	}

	@Override
	public String forPaginateFrom(String tableName, String where) {
		StringBuilder from = new StringBuilder(" FROM ");
		from.append("`" + tableName + "`");

		if (where != null && !"".equals(where.trim())) {
			from.append(" WHERE " + where);
		}
		return from.toString();
	}

	@Override
	public String forInsertWebName(String tablePrefix) {
		return "INSERT INTO `" + tablePrefix + "option` (option_key, option_value) VALUES ('web_name', ? )";
	}

	@Override
	public String forInsertFirstUser(String tablePrefix) {
		return "INSERT INTO `" + tablePrefix + "user` (username, password, salt, role, status, created) "
				+ "VALUES (?,?,?,?,?,?)";
	}

	@Override
	public DruidPlugin createDuidPlugin(String dbHost, String dbName, String dbUser, String dbPassword) {

		String jdbc_url = "jdbc:mysql://" + dbHost + "/" + dbName + "?" + "useUnicode=true&" + "characterEncoding=utf8&"
				+ "zeroDateTimeBehavior=convertToNull";

		DruidPlugin druidPlugin = new DruidPlugin(jdbc_url, dbUser, dbPassword);
		druidPlugin.addFilter(new StatFilter());

		WallFilter wall = new WallFilter();
		wall.setDbType("mysql");
		druidPlugin.addFilter(wall);

		return druidPlugin;
	}

	public String doTableConvert(String sql) {
		for (Map.Entry<String, String> entry : tableMapping.entrySet()) {
			sql = sql.replace(" " + entry.getKey() + " ", String.format(" `%s` ", entry.getValue()));
			sql = sql.replace(" " + entry.getKey() + ",", String.format(" `%s`,", entry.getValue()));
			sql = sql.replace(" " + entry.getKey() + ".", String.format(" `%s`.", entry.getValue()));
			sql = sql.replace("," + entry.getKey() + " ", String.format(",`%s` ", entry.getValue()));
			sql = sql.replace("," + entry.getKey() + ".", String.format(",`%s`.", entry.getValue()));
			// sql = sql.replace(entry.getKey() + "`", entry.getValue() + "`");
		}
		return sql;
	}

	@Override
	public String forInstall(String tablePrefix) {

		String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/mysql.sql";
		String sql_text = FileUtils.readString(new File(SqlFilePath)).replace("{table_prefix}", tablePrefix)
				.replace("{charset}", "utf8mb4");

		return sql_text;
	}

}
