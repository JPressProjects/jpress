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
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.support.jwt.JwtConfig;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.JbootWebConfig;
import io.jpress.commons.utils.CommonsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class InstallUtil {

    private static final Log log = Log.getLog(InstallUtil.class);

    private String dbName;
    private String dbUser;
    private String dbPassword;

    private boolean initBefore = false;
    private boolean jpressDb = false;

    private DbUtil dbUtil;
    private String jdbcUrl;


    public void init(
            String db_name,
            String db_user,
            String db_password,
            String db_host,
            String db_host_port) {

        dbUtil = new DbUtil(db_name, db_user, db_password, db_host, db_host_port);

        dbName = db_name;
        dbUser = db_user;
        dbPassword = db_password;

        jdbcUrl = "jdbc:mysql://" + db_host + ":" + db_host_port + "/" + dbName + "?"
                + "useUnicode=true&"
                + "useSSL=false&"
                + "characterEncoding=utf8&"
                + "zeroDateTimeBehavior=convertToNull";


        List<String> tables = getTableList();

        if (ArrayUtil.isNullOrEmpty(tables)) {
            setInitBefore(false);
            setJpressDb(true);
        } else if (tables.contains("attachment")
                && tables.contains("option")
                && tables.contains("menu")
                && tables.contains("permission")
                && tables.contains("role")
                && tables.contains("user")
                && tables.contains("utm")) {

            setInitBefore(true);
            setJpressDb(true);
        } else {
            setJpressDb(false);
        }

    }

    public File getLockFile() {
        return new File(PathKit.getRootClassPath(), "install.lock");
    }


    public boolean initJpressProperties() {

        File propertieFile = new File(PathKit.getRootClassPath(), "jboot.properties");

        Properties p = propertieFile.exists()
                ? PropKit.use("jboot.properties").getProperties()
                : new Properties();


        //jboot.app.mode
        putPropertie(p, "jboot.app.mode", "product");

        //cookieEncryptKey
        String cookieEncryptKey = StrUtil.uuid();
        if (putPropertie(p, "jboot.web.cookieEncryptKey", cookieEncryptKey)) {
            Jboot.config(JbootWebConfig.class).setCookieEncryptKey("cookieEncryptKey");
        }

        //jwtSecret
        String jwtSecret = StrUtil.uuid();
        if (putPropertie(p, "jboot.web.jwt.secret", jwtSecret)) {
            Jboot.config(JwtConfig.class).setSecret(jwtSecret);
        }

        p.put("jboot.datasource.type", "mysql");
        p.put("jboot.datasource.url", jdbcUrl);
        p.put("jboot.datasource.user", dbUser);

        if (dbPassword != null) {
            p.put("jboot.datasource.password", dbPassword);
        }


        return savePropertie(p, propertieFile);
    }

    private boolean putPropertie(Properties p, String key, String value) {
        Object v = p.get(key);
        if (v == null) {
            p.put(key, value);
            return true;
        }

        return false;
    }


    private boolean savePropertie(Properties p, File pFile) {
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


    public List<String> getTableList() {
        try {
            return dbUtil.query("show tables;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void initJPressTables() throws SQLException {
        String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/mysql.sql";
        String installSql = FileUtil.readString(new File(SqlFilePath));
        dbUtil.executeSql(installSql);
    }


    public DataSourceConfig getDataSourceConfig() {
        return dbUtil.getDataSourceConfig();
    }

    public void setInitBefore(boolean initBefore) {
        this.initBefore = initBefore;
    }

    public boolean isInitBefore() {
        return initBefore;
    }

    public boolean isJpressDb() {
        return jpressDb;
    }

    public void setJpressDb(boolean jpressDb) {
        this.jpressDb = jpressDb;
    }
}
