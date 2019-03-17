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
package io.jpress.core.addon;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import io.jboot.db.JbootDbManager;
import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.core.support.ehcache.EhcacheManager;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 插件工具类
 */
public class AddonUtil {

    private static final Log LOG = Log.getLog(AddonUtil.class);
    private static List<String> resourceSuffix = new ArrayList<String>();

    static {
        resourceSuffix.add(".html");
        resourceSuffix.add(".htm");
        resourceSuffix.add(".css");
        resourceSuffix.add(".js");
        resourceSuffix.add(".jpg");
        resourceSuffix.add(".jpeg");
        resourceSuffix.add(".png");
        resourceSuffix.add(".bmp");
        resourceSuffix.add(".gif");
        resourceSuffix.add(".webp");
        resourceSuffix.add(".svg");
        resourceSuffix.add(".ttf");
        resourceSuffix.add(".woff");
        resourceSuffix.add(".woff2");
        resourceSuffix.add(".webp");
        resourceSuffix.add(".sql");
    }

    private static boolean isResource(String name) {
        String suffix = FileUtil.getSuffix(name);
        return suffix != null && resourceSuffix.contains(suffix.toLowerCase());
    }

    public static File resourceFile(AddonInfo addonInfo, String path) {
        String basePath = PathKit.getWebRootPath()
                + File.separator
                + "addons"
                + File.separator
                + addonInfo.getId();

        return new File(basePath, path);
    }

    /**
     * 解压 zip 或者 jar 的资源文件
     *
     * @param addonInfo
     * @throws IOException
     */

    public static void unzipResources(AddonInfo addonInfo) throws IOException {
        String basePath = PathKit.getWebRootPath()
                + File.separator
                + "addons"
                + File.separator
                + addonInfo.getId();

        ZipFile zipFile = new ZipFile(addonInfo.buildJarFile());
        try {
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                while (entryEnum.hasMoreElements()) {
                    OutputStream os = null;
                    InputStream is = null;
                    try {
                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                        if (!zipEntry.isDirectory() && isResource(zipEntry.getName())) {
                            File targetFile = new File(basePath + File.separator + zipEntry.getName());
                            if (!targetFile.getParentFile().exists()) {
                                targetFile.getParentFile().mkdirs();
                            }
                            os = new BufferedOutputStream(new FileOutputStream(targetFile));
                            is = zipFile.getInputStream(zipEntry);
                            byte[] buffer = new byte[4096];
                            int readLen = 0;
                            while ((readLen = is.read(buffer, 0, 4096)) > 0) {
                                os.write(buffer, 0, readLen);
                            }
                        }
                    } finally {
                        CommonsUtils.quietlyClose(is, os);
                    }
                }
            }
        } finally {
            CommonsUtils.quietlyClose(zipFile);
        }
    }


    private static Map<String, AddonInfo> addonInfoCache = new ConcurrentHashMap<>();


    public static void clearAddonInfoCache(File addonFile) {
        addonInfoCache.remove(addonFile.getAbsolutePath());
    }

    public static AddonInfo readAddonInfo(File addonFile) {
        AddonInfo addonInfo = addonInfoCache.get(addonFile.getAbsolutePath());
        if (addonInfo == null) {
            addonInfo = readSimpleAddonInfo(addonFile);
            if (addonInfo == null) {
                return null;
            }
            AddonClassLoader classLoader = null;
            try {
                classLoader = new AddonClassLoader(addonInfo);
                List<String> classNameList = classLoader.getClassNameList();
                for (String className : classNameList) {
                    EhcacheManager.addMapping(className, classLoader);
                }
                classLoader.load();
            } catch (IOException e) {
                LogKit.error(e.toString(), e);
            } finally {
                //必须关闭，在Windows下才能卸载插件的时候删除jar包
                //否则一旦被 AddonClassLoader load之后，无法被删除
                CommonsUtils.quietlyClose(classLoader);
            }
            addonInfoCache.put(addonFile.getAbsolutePath(), addonInfo);
        }
        return addonInfo;
    }


    public static AddonInfo readSimpleAddonInfo(File addonFile) {
        ZipFile zipFile = null;
        Properties addonProp = null;
        Properties addonConfigProp = null;
        try {
            zipFile = new ZipFile(addonFile);
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                while (entryEnum.hasMoreElements()) {
                    InputStream is = null;
                    try {
                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                        if (StringUtils.equalsAnyIgnoreCase(zipEntry.getName(), "addon.txt", "addon.properties")) {
                            is = zipFile.getInputStream(zipEntry);
                            addonProp = new Properties();
                            addonProp.load(new InputStreamReader(is, "utf-8"));
                        } else if (StringUtils.equalsAnyIgnoreCase(zipEntry.getName(), "config.txt", "config.properties")) {
                            is = zipFile.getInputStream(zipEntry);
                            addonConfigProp = new Properties();
                            addonConfigProp.load(new InputStreamReader(is, "utf-8"));
                        }
                    } finally {
                        CommonsUtils.quietlyClose(is);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            CommonsUtils.quietlyClose(zipFile);
        }

        if (addonProp == null) {
            return null;
        }

        AddonInfo addonInfo = new AddonInfo(addonProp);
        if (addonConfigProp != null) {
            addonConfigProp.forEach((o, o2) -> {
                if (o != null && o2 != null) addonInfo.addConfig(o.toString(), o2.toString());
            });
        }

        return addonInfo;
    }

    /**
     * 批量执行 Sql
     *
     * @param addonInfo
     * @param sqlFilePath
     * @throws SQLException
     */
    public static void execSqlFile(AddonInfo addonInfo, String sqlFilePath) throws SQLException {
        File file = resourceFile(addonInfo, sqlFilePath);
        if (!file.exists()) {
            LOG.warn("file not exists : " + file);
            return;
        }
        String sql = FileUtil.readString(file);
        if (StrUtil.isBlank(sql)) {
            LOG.warn("can not read sql in : " + file);
            return;
        }
        execSql(addonInfo, sql);
    }

    /**
     * 执行 Sql，可能用于在插件安装的时候进行执行 Sql 创建表等
     * 支持 Sql 批量执行
     *
     * @param addonInfo
     * @param sql
     * @throws SQLException
     */
    public static void execSql(AddonInfo addonInfo, String sql) throws SQLException {
        DataSourceConfig dataSourceConfig = getDatasourceConfig(addonInfo);
        DataSource dataSource = new DataSourceBuilder(dataSourceConfig).build();

        Connection conn = dataSource.getConnection();
        Statement pst = null;
        try {
            pst = conn.createStatement();
            sql = StrUtil.requireNonBlank(sql, "sql must not be null or blank.");
            if (sql.contains(";")) {
                String sqls[] = sql.split(";");
                for (String s : sqls) {
                    if (StrUtil.isNotBlank(s)) {
                        pst.addBatch(s);
                    }
                }
            } else {
                pst.addBatch(sql);
            }
        } finally {
            pst.executeBatch();
            CommonsUtils.quietlyClose(pst, conn);
        }
    }


    public static ActiveRecordPlugin createRecordPlugin(AddonInfo addonInfo) {
        DataSourceConfig config = getDatasourceConfig(addonInfo);
        config.setName(addonInfo.getId());
        config.setNeedAddMapping(false);
        return JbootDbManager.me().createRecordPlugin(config);
    }


    private static DataSourceConfig getDatasourceConfig(AddonInfo addonInfo) {

        Map<String, String> config = addonInfo.getConfig();
        if (config == null || config.isEmpty()) {
            return DataSourceConfigManager.me().getMainDatasourceConfig();
        }

        String url = config.get("db.url");
        String user = config.get("db.user");

        /**
         * must need url and user
         */
        if (config == null || StrUtil.isBlank(url) || StrUtil.isBlank(user)) {
            return DataSourceConfigManager.me().getMainDatasourceConfig();
        }


        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        dsc.setUser(user);
        dsc.setPassword(config.get("db.password"));
        dsc.setConnectionInitSql(config.get("db.connectionInitSql"));
        dsc.setPoolName(config.get("db.poolName"));
        dsc.setSqlTemplate(config.get("db.sqlTemplate"));
        dsc.setSqlTemplatePath(config.get("db.sqlTemplatePath"));
        dsc.setFactory(config.get("db.factory"));
        dsc.setShardingConfigYaml(config.get("db.shardingConfigYaml"));
        dsc.setDbProFactory(config.get("db.dbProFactory"));
        dsc.setTable(config.get("db.table"));
        dsc.setExTable(config.get("db.exTable"));
        dsc.setDialectClass(config.get("db.dialectClass"));
        dsc.setActiveRecordPluginClass(config.get("db.activeRecordPluginClass"));

        String cachePrepStmts = config.get("db.cachePrepStmts");
        String prepStmtCacheSize = config.get("db.prepStmtCacheSize");
        String prepStmtCacheSqlLimit = config.get("db.prepStmtCacheSqlLimit");
        String maximumPoolSize = config.get("db.maximumPoolSize");
        String maxLifetime = config.get("db.maxLifetime");
        String minimumIdle = config.get("db.minimumIdle");

        if (StrUtil.isNotBlank(cachePrepStmts)) {
            dsc.setCachePrepStmts(Boolean.valueOf(cachePrepStmts));
        }

        if (StrUtil.isNotBlank(prepStmtCacheSize)) {
            dsc.setPrepStmtCacheSize(Integer.valueOf(cachePrepStmts));
        }

        if (StrUtil.isNotBlank(prepStmtCacheSqlLimit)) {
            dsc.setPrepStmtCacheSqlLimit(Integer.valueOf(prepStmtCacheSqlLimit));
        }

        if (StrUtil.isNotBlank(maximumPoolSize)) {
            dsc.setMaximumPoolSize(Integer.valueOf(maximumPoolSize));
        }

        if (StrUtil.isNotBlank(maxLifetime)) {
            dsc.setMaxLifetime(Long.valueOf(maxLifetime));
        }

        if (StrUtil.isNotBlank(minimumIdle)) {
            dsc.setMinimumIdle(Integer.valueOf(minimumIdle));
        }

        String type = config.get("type");
        String driverClassName = config.get("driverClassName");

        if (StrUtil.isNotBlank(type)) {
            dsc.setType(type);
        }

        if (StrUtil.isNotBlank(driverClassName)) {
            dsc.setDriverClassName(driverClassName);
        }

        return dsc;
    }

    /**
     * 在windows系统下，当去上传刚刚 stop 的插件的时候，可能被占用无法删除
     * 但是过 "一段时间" 后，又可以删除了
     * <p>
     * 原因是：Classloader 进行 close() 的时候，无法及时释放资源造成的
     *
     * @param file
     * @return
     */
    public static boolean forceDelete(File file) {
        if (!file.exists()) {
            return false;
        }

        boolean result = file.delete();
        if (result ) {
            return true;
        }
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = file.delete();
        }
        return result;
    }

}
