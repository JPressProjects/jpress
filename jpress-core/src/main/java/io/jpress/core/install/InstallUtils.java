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
import io.jboot.Jboot;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.exception.JbootException;
import io.jboot.support.jwt.JwtConfig;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.JbootWebConfig;
import io.jpress.commons.utils.CommonsUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
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

    private static boolean initBefore = false;

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


        jdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbHostPort + "/" + dbName + "?"
                + "useUnicode=true&"
                + "useSSL=false&"
                + "characterEncoding=utf8&"
                + "zeroDateTimeBehavior=convertToNull";

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


        //jboot.app.mode
        putPropertieIfValueIsNull(p, "jboot.app.mode", "product");

        //cookieEncryptKey
        String cookieEncryptKey = StrUtil.uuid();
        if (putPropertieIfValueIsNull(p, "jboot.web.cookieEncryptKey", cookieEncryptKey)) {
            Jboot.config(JbootWebConfig.class).setCookieEncryptKey("cookieEncryptKey");
        }

        //jwtSecret
        String jwtSecret = StrUtil.uuid();
        if (putPropertieIfValueIsNull(p, "jboot.web.jwt.secret", jwtSecret)) {
            Jboot.config(JwtConfig.class).setSecret(jwtSecret);
        }

        p.put("jboot.datasource.type", "mysql");
        p.put("jboot.datasource.url", jdbcUrl);
        p.put("jboot.datasource.user", dbUser);

        if (dbPassword != null) {
            p.put("jboot.datasource.password", dbPassword);
        }


        return save(p, propertieFile);
    }

    private static boolean putPropertieIfValueIsNull(Properties p, String key, String value) {
        Object v = p.get(key);
        if (v == null) {
            p.put(key, value);
            return true;
        }

        return false;
    }


    private static boolean save(Properties p, File pFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pFile);
            p.store(fos, "Auto create by JPress");
        } catch (Exception e) {
            log.warn(e.toString(), e);
            return false;
        } finally {
            CommonsUtils.quietlyClose(fos);
        }
        return true;
    }


    public static List<String> getTableList() throws SQLException {
        Connection conn = getConnection();
        List<String> tableList = query(conn, "show tables;");
        conn.close();
        return tableList;
    }


    public static void tryInitJPressTables() throws SQLException {
        String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/mysql.sql";
        String installSql = FileUtil.readString(new File(SqlFilePath));
        executeBatchSql(installSql);
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
            CommonsUtils.quietlyClose(pst, conn);
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
            CommonsUtils.quietlyClose(rs, pst);
        }

        return result;
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

    public static void setInitBefore(boolean initBefore) {
        InstallUtils.initBefore = initBefore;
    }

    public static boolean isInitBefore() {
        return initBefore;
    }
}
