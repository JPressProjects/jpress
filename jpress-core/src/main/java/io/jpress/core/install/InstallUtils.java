/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.install;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.exception.JbootException;
import io.jboot.utils.FileUtils;
import io.jboot.utils.StrUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class InstallUtils {

    private static final Log log = Log.getLog(InstallUtils.class);
    private static String dbHost;
    private static String dbHostPort;
    private static String dbName;
    private static String dbUser;
    private static String dbPassword;

    private static DataSource dataSource;
    private static DataSourceConfig dataSourceConfig;
    private static String jdbcUrl;


    public static void init(
            String db_name,
            String db_user,
            String db_password,
            String db_host,
            String db_host_port) {


        dbName = db_name;
        dbUser = db_user;
        dbPassword = db_password;
        dbHost = db_host;
        dbHostPort = db_host_port;


        jdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbHostPort + "/" + dbName + "?" + "useUnicode=true&"
                + "characterEncoding=utf8&" + "zeroDateTimeBehavior=convertToNull";

        dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(jdbcUrl);
        dataSourceConfig.setUser(db_user);
        dataSourceConfig.setPassword(db_password);


        dataSource = new DataSourceBuilder(dataSourceConfig).build();

    }

    public static File lockFile() {
        return new File(PathKit.getRootClassPath(), "install.lock");
    }


    public static boolean initJpressProperties() {

        File propertieFile = new File(PathKit.getRootClassPath(), "jboot.properties");

        Properties p = propertieFile.exists()
                ? PropKit.use("jboot.properties").getProperties()
                : new Properties();

        p.put("jboot.mode", "product");
        p.put("jboot.model.idCacheEnable", "true");

        String uuid = StrUtils.uuid();
        p.put("jboot.web.cookieEncryptKey", uuid);
        p.put("jboot.web.jwt.secret", uuid);

        p.put("jboot.datasource.type", "mysql");
        p.put("jboot.datasource.url", jdbcUrl);
        p.put("jboot.datasource.user", dbUser);
        p.put("jboot.datasource.password", dbPassword);

        return save(p, propertieFile);
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
        Connection conn = getConnection();
        List<String> tableList = query(conn, "show tables;");
        conn.close();
        return tableList;
    }


    public static void initJPressTables() throws SQLException {
        String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/mysql.sql";
        String installSql = FileUtils.readString(new File(SqlFilePath));
        executeBatchSql(installSql);
    }


    public static void executeSQL(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
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
            close(pstmt, conn);
        }
    }

    private static void executeBatchSql(String batchSql) throws SQLException {
        Connection conn = getConnection();
        Statement pst = null;
        try {
            pst = conn.createStatement();
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
        } finally {
            pst.executeBatch();
            close(pst, conn);
        }
    }

    private static <T> List<T> query(Connection conn, String sql) throws SQLException {
        List result = new ArrayList();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
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
        } finally {
            close(rs, pst);
        }

        return result;
    }


    private static final void close(AutoCloseable... sts) {
        for (AutoCloseable st : sts)
            if (st != null) {
                try {
                    st.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new JbootException(e);
        }
    }

    public static DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }
}
