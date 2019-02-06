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

import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.db.JbootDbManager;
import io.jboot.db.annotation.Table;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jpress.core.addon.controller.AddonControllerManager;
import io.jpress.core.addon.handler.AddonHandlerManager;
import io.jpress.core.addon.interceptor.AddonInterceptorManager;
import io.jpress.core.install.Installer;
import io.jpress.service.OptionService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 插件管理器：安装、卸载、启用、停用
 * <p>
 * 安装的动作：
 * 1、解压jar资源文件
 * 2、解压成功后，回调插件的 onInstall()
 * 3、回调成功后，标识该插件已经安装
 * <p>
 * 卸载的动作：
 * 1、回调插件的 onUninstall()
 * 2、回调成功后删除jar文件以及资源文件
 * 3、删除安装、启动、停止等标识
 * <p>
 * 启用插件：
 * 1、标识插件启用
 * 2、添加插件的 controller、handler、interceptor 到对应的管理器
 * 3、回调插件的 onStart()
 * 4、每次重启 JPress 都会调用 onStart()
 * <p>
 * 停用插件：
 * 1、删除插件的 controller、handler、interceptor
 * 2、删除插件启用标识
 * 3、回调插件的 onStop()
 * <p>
 * <p>
 * 插件开发：
 * 1、插件的作者可以在 onInstall() 进行数据库表创建等工作
 * 2、插件的作者可以在 onUninstall() 进行数据库表删除和其他资源删除等工作
 */
public class AddonManager implements JbootEventListener {

    private static final AddonManager me = new AddonManager();

    public static AddonManager me() {
        return me;
    }

    private Set<AddonInfo> addonInfoList = new HashSet<>();

    public void init() {

        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }

        initLoad();
    }

    /**
     * 启动的时候，加载所有插件
     * 备注：插件可能是复制到插件目录下，而非通过后台进行 "安装"
     */
    private void initLoad() {

        File addonDir = new File(PathKit.getWebRootPath(), "WEB-INF/addons");
        if (!addonDir.exists()) return;

        File[] addonJarFiles = addonDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (addonJarFiles == null || addonJarFiles.length == 0) return;

        for (File jarFile : addonJarFiles) {
            AddonInfo addonInfo = AddonUtil.readAddonInfo(jarFile);
            if (addonInfo != null) addonInfoList.add(addonInfo);
        }

        installInit(addonJarFiles);
        startInit(addonJarFiles);
    }

    public AddonInfo getAddonInfo(String id) {
        for (AddonInfo addonInfo : addonInfoList) {
            if (id.equals(addonInfo.getId())) return addonInfo;
        }

        return null;
    }

    public List<AddonInfo> getAllAddonInfos() {
        return new ArrayList<>(addonInfoList);
    }


    private void installInit(File[] addonJars) {

        OptionService optionService = Aop.get(OptionService.class);

        for (File jarFile : addonJars) {
            AddonInfo addonInfo = AddonUtil.readAddonInfo(jarFile);
            if (addonInfo != null && optionService.findByKey("addonInstall:" + addonInfo.getId()) != null) {
                addonInfo.setStatus(AddonInfo.STATUS_INSTALL);
            }
        }
    }

    private void startInit(File[] addonJars) {
        OptionService optionService = Aop.get(OptionService.class);
        for (File jarFile : addonJars) {
            AddonInfo addonInfo = AddonUtil.readAddonInfo(jarFile);
            if (addonInfo != null && optionService.findByKey("addonStart:" + addonInfo.getId()) != null) {
                doStart(addonInfo);
            }
        }
    }

    public boolean install(String id) {
        return install(getAddonInfo(id).buildJarFile());
    }

    /**
     * 安装插件：
     * 1、解压jar资源文件
     * 2、解压成功后，回调插件的 onInstall()
     * 3、回调成功后，标识该插件已经安装
     *
     * @param jarFile
     * @return
     */

    public boolean install(File jarFile) {

        AddonInfo addonInfo = AddonUtil.readAddonInfo(jarFile);
        addonInfoList.add(addonInfo);

        Addon addon = Aop.get(addonInfo.getAddonClass());

        try {
            AddonUtil.unzipResources(addonInfo);
            if (addon != null) addon.onInstall(addonInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        addonInfo.setStatus(AddonInfo.STATUS_INSTALL);

        OptionService optionService = Aop.get(OptionService.class);
        return optionService.saveOrUpdate("addonInstall:" + addonInfo.getId(), "true") != null;

    }


    public boolean uninstall(String id) {
        return uninstall(getAddonInfo(id));
    }

    /**
     * 卸载的动作：
     * 1、回调插件的 onUninstall()
     * 2、回调成功后删除jar文件以及资源文件
     * 3、删除安装、启动、停止等标识
     *
     * @param addonInfo
     * @return
     */
    public boolean uninstall(AddonInfo addonInfo) {

        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) addon.onUninstall(addonInfo);

        //删除jar包
        addonInfo.buildJarFile().delete();

        //删除已解压缩的资源文件
        FileUtils.deleteQuietly(new File(PathKit.getWebRootPath(), "addons/" + addonInfo.getId()));

        addonInfo.setStatus(AddonInfo.STATUS_INIT);

        //删除插件列表缓存
        addonInfoList.remove(addonInfo);

        OptionService optionService = Aop.get(OptionService.class);
        return optionService.deleteByKey("addonInstall:" + addonInfo.getId());

    }


    public boolean start(String addonInfoId) {
        return start(getAddonInfo(addonInfoId));
    }

    /**
     * 启动的动作：
     * 1、标识插件启用
     * 2、添加插件的 controller、handler、interceptor 到对应的管理器
     * 3、若有 Model 类，启动新的 arp 插件
     * 4、回调插件的 onStart()
     * 5、构建后台或用户中心菜单
     *
     * 备注：
     * 每次重启 JPress 都会调用 onStart()
     *
     * @param addonInfo
     * @return
     */
    public boolean start(AddonInfo addonInfo) {
        OptionService optionService = Aop.get(OptionService.class);
        optionService.saveOrUpdate("addonStart:" + addonInfo.getId(), "true");

        doStart(addonInfo);

        return true;
    }

    private void doStart(AddonInfo addonInfo) {
        List<Class<? extends Controller>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends Controller> c : controllerClasses)
                AddonControllerManager.addController(c, addonInfo.getId());
        }


        List<Class<? extends Handler>> handlerClasses = addonInfo.getHandlers();
        if (handlerClasses != null) {
            for (Class<? extends Handler> c : handlerClasses)
                AddonHandlerManager.addHandler(c);
        }

        List<Class<? extends Interceptor>> interceptorClasses = addonInfo.getInterceptors();
        if (interceptorClasses != null) {
            for (Class<? extends Interceptor> c : interceptorClasses)
                AddonInterceptorManager.addInterceptor(c);
        }

        List<Class<? extends JbootModel>> modelClasses = addonInfo.getModels();
        if (modelClasses != null && !modelClasses.isEmpty()) {

            DataSourceConfig config = getDatasourceConfig(addonInfo);
            config.setNeedAddMapping(false);
            ActiveRecordPlugin arp = JbootDbManager.me().createRecordPlugin(config);

            for (Class<? extends JbootModel> c : modelClasses) {
                Table table = c.getAnnotation(Table.class);
                arp.addMapping(AnnotationUtil.get(table.tableName()), (Class<? extends Model<?>>) c);
            }

            addonInfo.setArp(arp);
            arp.start();
        }

        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) addon.onStart(addonInfo);

        AddonControllerManager.buildActionMapping();

        addonInfo.setStatus(AddonInfo.STATUS_START);
    }

    private DataSourceConfig getDatasourceConfig(AddonInfo addonInfo){

        Map<String,String> config = addonInfo.getConfig();

        String url = config.get("db.url");
        String user = config.get("db.user");
        /**
         * must need url and user
         */
        if (config == null || StrUtil.isBlank(url) || StrUtil.isBlank(user)){
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

        String cachePrepStmts = config.get("cachePrepStmts");
        String prepStmtCacheSize = config.get("prepStmtCacheSize");
        String prepStmtCacheSqlLimit = config.get("prepStmtCacheSqlLimit");
        String maximumPoolSize = config.get("maximumPoolSize");
        String maxLifetime = config.get("maxLifetime");
        String minimumIdle = config.get("minimumIdle");

        if (StrUtil.isNotBlank(cachePrepStmts)){
            dsc.setCachePrepStmts(Boolean.valueOf(cachePrepStmts));
        }

        if (StrUtil.isNotBlank(prepStmtCacheSize)){
            dsc.setPrepStmtCacheSize(Integer.valueOf(cachePrepStmts));
        }

        if (StrUtil.isNotBlank(prepStmtCacheSqlLimit)){
            dsc.setPrepStmtCacheSqlLimit(Integer.valueOf(prepStmtCacheSqlLimit));
        }

        if (StrUtil.isNotBlank(maximumPoolSize)){
            dsc.setMaximumPoolSize(Integer.valueOf(maximumPoolSize));
        }

        if (StrUtil.isNotBlank(maxLifetime)){
            dsc.setMaxLifetime(Long.valueOf(maxLifetime));
        }

        if (StrUtil.isNotBlank(minimumIdle)){
            dsc.setMinimumIdle(Integer.valueOf(minimumIdle));
        }

        String type = config.get("type");
        String driverClassName = config.get("driverClassName");

        if (StrUtil.isNotBlank(type)){
            dsc.setType(type);
        }

        if (StrUtil.isNotBlank(driverClassName)){
            dsc.setDriverClassName(driverClassName);
        }

        return dsc;
    }


    public boolean stop(String id) {
        return stop(getAddonInfo(id));
    }


    public boolean stop(AddonInfo addonInfo) {


        OptionService optionService = Aop.get(OptionService.class);
        optionService.deleteByKey("addonStart:" + addonInfo.getId());

        List<Class<? extends Controller>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends Controller> c : controllerClasses)
                AddonControllerManager.deleteController(c);
        }


        List<Class<? extends Handler>> handlerClasses = addonInfo.getHandlers();
        if (handlerClasses != null) {
            for (Class<? extends Handler> c : handlerClasses)
                AddonHandlerManager.deleteHandler(c);
        }

        List<Class<? extends Interceptor>> interceptorClasses = addonInfo.getInterceptors();
        if (interceptorClasses != null) {
            for (Class<? extends Interceptor> c : interceptorClasses)
                AddonInterceptorManager.deleteInterceptor(c);
        }

        if (addonInfo.getArp() != null) {
            addonInfo.getArp().stop();
        }

        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) addon.onStop(addonInfo);

        AddonControllerManager.buildActionMapping();

        addonInfo.setStatus(AddonInfo.STATUS_INSTALL);

        return true;

    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }
}
