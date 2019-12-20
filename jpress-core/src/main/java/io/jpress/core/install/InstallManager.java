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
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.FileUtil;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * @author yang fuhai
 */
public class InstallManager {


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
            String dbName,
            String dbUser,
            String dbPassword,
            String dbHost,
            int dbHostPort) {

        dbExecuter = new DbExecuter(dbName, dbUser, dbPassword, dbHost, dbHostPort);

        List<String> tables = dbExecuter.queryTables();

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

    public boolean isInited(){
        return dbExecuter != null;
    }


    public void doInitDatabase() throws SQLException {
        String sqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/install.sql";
        String installSql = FileUtil.readString(new File(sqlFilePath));
        dbExecuter.executeSql(installSql);
    }


    public void doUpgradeDatabase() throws SQLException {
        String sqlFilePath = PathKit.getWebRootPath() + "/WEB-INF/install/sqls/";
        String upgradeSql = FileUtil.readString(new File(sqlFilePath, upgradeSqlFileName));
        dbExecuter.executeSql(upgradeSql);
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

    public DbExecuter getDbExecuter() {
        return dbExecuter;
    }
}
