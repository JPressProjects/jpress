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
import io.jboot.utils.CookieUtil;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.JbootWebConfig;
import io.jpress.commons.utils.CommonsUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class InstallManager {

    private static final Log log = Log.getLog(InstallManager.class);

    private String jdbcUrl;
    private String dbName;
    private String dbUser;
    private String dbPassword;

    private DbExecuter dbExecuter;

    private boolean dbExist = false;
    private boolean isJPressDb = false;
    private boolean isNeedUpgrade = false;
    private String upgradeSqlFileName;


    private static final InstallManager me = new InstallManager();

    public static final InstallManager me() {
        return me;
    }


    public void init(
            String db_name,
            String db_user,
            String db_password,
            String db_host,
            String db_host_port) {

        dbExecuter = new DbExecuter(db_name, db_user, db_password, db_host, db_host_port);

        dbName = db_name;
        dbUser = db_user;
        dbPassword = db_password;

        jdbcUrl = "jdbc:mysql://" + db_host + ":" + db_host_port + "/" + dbName + "?"
                + "useUnicode=true&"
                + "useSSL=false&"
                + "characterEncoding=utf8&"
                + "zeroDateTimeBehavior=convertToNull";


        List<String> tables = getTableList();

        //空数据库
        if (ArrayUtil.isNullOrEmpty(tables)) {

            dbExist = false;
            isJPressDb = false;
            isNeedUpgrade = false;
        }

        //已经是 v3 版本
        else if (tables.containsAll(Consts.V3_TABLES)) {
            dbExist = true;
            isJPressDb = true;
            isNeedUpgrade = false;
        }

        //2.x 版本
        else if (tables.containsAll(Consts.V2_TABLES)) {
            dbExist = true;
            isJPressDb = true;
            isNeedUpgrade = true;
            upgradeSqlFileName = "v2_upgrade.sql";
        }


        //其他数据库
        else {

            dbExist = true;
            isJPressDb = false;
        }

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
            CookieUtil.initEncryptKey(cookieEncryptKey);
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
            return dbExecuter.query("show tables;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void doInitDatabase() throws SQLException {
        String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/install.sql";
        String installSql = FileUtil.readString(new File(SqlFilePath));
        dbExecuter.executeSql(installSql);
    }


    public void doUpgradeDatabase() throws SQLException {
        String SqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/";
        String installSql = FileUtil.readString(new File(SqlFilePath, upgradeSqlFileName));
        dbExecuter.executeSql(installSql);
    }

    public DataSourceConfig getDataSourceConfig() {
        return dbExecuter.getDataSourceConfig();
    }


    public boolean isDbExist() {
        return dbExist;
    }

    public boolean isJPressDb() {
        return isJPressDb;
    }

    public boolean isNeedUpgrade() {
        return isNeedUpgrade;
    }
}
