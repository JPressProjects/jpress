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
import com.jfinal.kit.SyncWriteMap;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.template.expr.ast.FieldKit;
import com.jfinal.template.expr.ast.MethodKit;
import io.jboot.Jboot;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.AnnotationUtil;
import io.jpress.core.addon.controller.AddonControllerManager;
import io.jpress.core.addon.handler.AddonHandlerManager;
import io.jpress.core.addon.interceptor.AddonInterceptorManager;
import io.jpress.core.install.Installer;
import io.jpress.service.OptionService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final Log LOG = Log.getLog(AddonManager.class);

    private static final String ADDON_INSTALL_PREFFIX = "addon-install:";
    private static final String ADDON_START_PREFFIX = "addon-start:";

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

        doInitInstalledAddons();
    }

    /**
     * 启动的时候，加载所有插件
     * 备注：插件可能是复制到插件目录下，而非通过后台进行 "安装"
     */
    private void doInitInstalledAddons() {

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
            if (addonInfo != null && optionService.findByKey(ADDON_INSTALL_PREFFIX + addonInfo.getId()) != null) {
                addonInfo.setStatus(AddonInfo.STATUS_INSTALL);
            }
        }
    }

    private void startInit(File[] addonJars) {
        OptionService optionService = Aop.get(OptionService.class);
        for (File jarFile : addonJars) {
            AddonInfo addonInfo = AddonUtil.readAddonInfo(jarFile);
            if (addonInfo != null && optionService.findByKey(ADDON_START_PREFFIX + addonInfo.getId()) != null) {
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
        try {
            AddonInfo addonInfo = AddonUtil.readAddonInfo(jarFile);
            addonInfoList.add(addonInfo);
            Addon addon = Aop.get(addonInfo.getAddonClass());
            AddonUtil.unzipResources(addonInfo);
            if (addon != null) {
                addon.onInstall(addonInfo);
            }

            addonInfo.setStatus(AddonInfo.STATUS_INSTALL);
            OptionService optionService = Aop.get(OptionService.class);

            return optionService.saveOrUpdate(ADDON_INSTALL_PREFFIX + addonInfo.getId(), "true") != null;
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        return false;

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

        AddonUtil.clearAddonInfoCache(addonInfo.buildJarFile());

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
        return optionService.deleteByKey(ADDON_INSTALL_PREFFIX + addonInfo.getId());

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
     * <p>
     * 备注：
     * 每次重启 JPress 都会调用 onStart()
     *
     * @param addonInfo
     * @return
     */
    public boolean start(AddonInfo addonInfo) {
        OptionService optionService = Aop.get(OptionService.class);
        optionService.saveOrUpdate(ADDON_START_PREFFIX + addonInfo.getId(), "true");

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

            ActiveRecordPlugin arp = addonInfo.createOrGetArp();

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


    public boolean stop(String id) {
        return stop(getAddonInfo(id));
    }


    public boolean stop(AddonInfo addonInfo) {

        OptionService optionService = Aop.get(OptionService.class);
        optionService.deleteByKey(ADDON_START_PREFFIX + addonInfo.getId());

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

        ActiveRecordPlugin arp = addonInfo.getArp();
        if (arp != null) {
            arp.stop();
            List<com.jfinal.plugin.activerecord.Table> tableList = getTableList(arp);
            if (tableList != null) {
                tableList.forEach(table -> {
                    // 必须要移除所有的缓存，否则当插件卸载重新安装的时候，
                    // 缓存里的可能还存在数据，由于可能是内存缓存，所有可能导致Class转化异常的问题
                    // PS：每次新安装的插件，都是一个新的 Classloader
                    Jboot.getCache().removeAll(table.getName());
                });
            }
        }

        // 清除模板引擎的 field 和 method 缓存
        // 否则可能会出现  object is not an instance of declaring class 的异常
        // https://gitee.com/fuhai/jpress/issues/IS5YQ
        try {
            Field fieldGetterCacheField = FieldKit.class.getDeclaredField("fieldGetterCache");
            fieldGetterCacheField.setAccessible(true);
            SyncWriteMap fieldGetterCacheMap = (SyncWriteMap) fieldGetterCacheField.get(null);
            fieldGetterCacheMap.clear();

            Field methodCacheField = MethodKit.class.getDeclaredField("methodCache");
            methodCacheField.setAccessible(true);
            SyncWriteMap methodCacheMap = (SyncWriteMap) methodCacheField.get(null);
            methodCacheMap.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) addon.onStop(addonInfo);

        AddonControllerManager.buildActionMapping();

        addonInfo.setStatus(AddonInfo.STATUS_INSTALL);

        return true;
    }


    private static List<com.jfinal.plugin.activerecord.Table> getTableList(ActiveRecordPlugin arp) {
        try {
            Field field = ActiveRecordPlugin.class.getDeclaredField("tableList");
            field.setAccessible(true);
            return (List<com.jfinal.plugin.activerecord.Table>) field.get(arp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }
}
