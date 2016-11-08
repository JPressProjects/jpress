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
package io.jpress.install;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.filter.stat.StatFilter;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.FreeMarkerRender;

//import io.jpress.core.db.MysqlDialect;
import io.jpress.utils.DateUtils;
import io.jpress.utils.FileUtils;

public class InstallUtils {
	private static final Log log = Log.getLog(InstallUtils.class);
	private static String dbHost;
	private static String dbHostPort;
	private static String dbName;
	private static String dbUser;
	private static String dbPassword;
	public static String dbTablePrefix;


	public static void init(String db_host, String db_host_port, String db_name, String db_user, String db_password,
			String db_tablePrefix) {
		dbHost = db_host;
		dbHostPort = db_host_port;
		dbName = db_name;
		dbUser = db_user;
		dbPassword = db_password;
		dbTablePrefix = db_tablePrefix;
	}

	public static boolean createDbProperties() {
		Properties p = new Properties();
		p.put("db_host", dbHost);
		p.put("db_host_port", dbHostPort);
		p.put("db_name", dbName);
		p.put("db_user", dbUser);
		p.put("db_password", dbPassword);
		p.put("db_tablePrefix", dbTablePrefix);
		File pFile = new File(PathKit.getRootClassPath(), "db.properties");
		return save(p, pFile);
	}

	public static boolean createJpressProperties() {
		Properties p = PropKit.use("jpress.properties").getProperties();
		p.put("dev_mode", "false");
		p.put("encrypt_key", UUID.randomUUID().toString());
		File pFile = new File(PathKit.getRootClassPath(), "jpress.properties");
		return save(p, pFile);
	}

	private static boolean save(Properties p, File pFile) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(pFile);
			p.store(fos, "Auto create by JPress");
		} catch (Exception e) {
			log.warn("InstallUtils save erro", e);
			return false;
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
				}
		}
		return true;
	}

	public static List<String> getTableList() throws SQLException {
		DruidPlugin dp = createDruidPlugin();
		Connection conn = dp.getDataSource().getConnection();
		List<String> tableList = query(conn, forShowTable());
		conn.close();
		dp.stop();
		return tableList;
	}

	public static void createJpressDatabase() throws SQLException {
		String installSql = forInstall(dbTablePrefix);
		DruidPlugin dp = createDruidPlugin();
		Connection conn = dp.getDataSource().getConnection();
		executeBatchSql(conn, installSql);
		conn.close();
		dp.stop();

	}

	public static void setWebName(String webName) throws SQLException {

		executeSQL(forInsertWebName(dbTablePrefix), webName);
	}

	public static void setWebFirstUser(String username, String password, String salt) throws SQLException {

		executeSQL(forInsertFirstUser(dbTablePrefix), username, password, salt, "administrator", "activited",
				DateUtils.now());
	}

	public static void executeSQL(String sql, Object... params) throws SQLException {
		DruidPlugin dp = createDruidPlugin();
		Connection conn = dp.getDataSource().getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (null != params && params.length > 0) {
				int i = 0;
				for (Object param : params) {
					pstmt.setString(++i, param.toString());
				}
			}
			pstmt.executeUpdate();

		} catch (SQLException e) {
			log.warn("InstallUtils executeSQL erro", e);
		} finally {
			pstmt.close();
			conn.close();
			dp.stop();
		}
	}

	private static void executeBatchSql(Connection conn, String batchSql) throws SQLException {
		Statement pst = conn.createStatement();
		if (null == batchSql) {
			throw new SQLException("SQL IS NULL");
		}

		if (batchSql.contains(";")) {
			String sqls[] = batchSql.split(";");
			for (String sql : sqls) {
				if (null != sql && !"".equals(sql.trim()))
					pst.addBatch(sql);
			}
		} else {
			pst.addBatch(batchSql);
		}
		pst.executeBatch();
		close(pst);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> List<T> query(Connection conn, String sql) throws SQLException {
		List result = new ArrayList();
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet rs = pst.executeQuery();
		int colAmount = rs.getMetaData().getColumnCount();
		if (colAmount > 1) {
			while (rs.next()) {
				Object[] temp = new Object[colAmount];
				for (int i = 0; i < colAmount; i++) {
					temp[i] = rs.getObject(i + 1);
				}
				result.add(temp);
			}
		} else if (colAmount == 1) {
			while (rs.next()) {
				result.add(rs.getObject(1));
			}
		}
		close(rs, pst);
		return result;
	}

	private static final void close(ResultSet rs, Statement st) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
			}
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
			}
	}

	private static final void close(Statement st) {
		if (st != null)
			try {
				st.close();
			} catch (SQLException e) {
			}
	}

	private static DruidPlugin createDruidPlugin() {
		DruidPlugin plugin = createDuidPlugin(dbHost, dbHostPort, dbName, dbUser, dbPassword);

		plugin.start();

		return plugin;
	}

	public static void renderInstallFinished(HttpServletRequest request, HttpServletResponse response,
			boolean[] isHandled) {
		isHandled[0] = true;
		// 这里不能用 JFreeMarkerRender，否则出现安装完成后缓存安装页面。
		new FreeMarkerRender("/WEB-INF/install/finished.html").setContext(request, response).render();
	}
	
	
	private static String forShowTable() {
		return "show tables;";
	}
	
	private static String forInstall(String tablePrefix) {

		String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/mysql.sql";
		String sql_text = FileUtils.readString(new File(SqlFilePath)).replace("{table_prefix}", tablePrefix)
				.replace("{charset}", "utf8mb4");

		return sql_text;
	}
	
	private static String forInsertWebName(String tablePrefix) {
		return "INSERT INTO `" + tablePrefix + "option` (option_key, option_value) VALUES ('web_name', ? )";
	}

	private static String forInsertFirstUser(String tablePrefix) {
		return "INSERT INTO `" + tablePrefix + "user` (username, password, salt, role, status, created) "
				+ "VALUES (?,?,?,?,?,?)";
	}

	private static DruidPlugin createDuidPlugin(String dbHost, String dbHostPort, String dbName, String dbUser,
			String dbPassword) {

		String jdbc_url = "jdbc:mysql://" + dbHost + ":" + dbHostPort + "/" + dbName + "?" + "useUnicode=true&"
				+ "characterEncoding=utf8&" + "zeroDateTimeBehavior=convertToNull";

		DruidPlugin druidPlugin = new DruidPlugin(jdbc_url, dbUser, dbPassword);
		druidPlugin.addFilter(new StatFilter());

		return druidPlugin;
	}

}
