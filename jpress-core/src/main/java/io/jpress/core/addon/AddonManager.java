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
import com.jfinal.kit.Ret;
import com.jfinal.kit.SyncWriteMap;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Directive;
import com.jfinal.template.expr.ast.FieldKit;
import com.jfinal.template.expr.ast.MethodKit;
import io.jboot.Jboot;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;
import io.jboot.db.model.JbootModelConfig;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jpress.core.addon.controller.AddonControllerManager;
import io.jpress.core.addon.handler.AddonHandlerManager;
import io.jpress.core.addon.interceptor.AddonInterceptorManager;
import io.jpress.core.install.Installer;
import io.jpress.core.wechat.WechatAddon;
import io.jpress.core.wechat.WechatAddonConfig;
import io.jpress.core.wechat.WechatAddonInfo;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.service.OptionService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private Map<String, AddonInfo> addonsCache = new ConcurrentHashMap<>();

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
            if (addonInfo != null && StrUtil.isNotBlank(addonInfo.getId())) {
                addonsCache.put(addonInfo.getId(), addonInfo);
            }
        }

        installInit(addonJarFiles);
        startInit(addonJarFiles);
    }

    public AddonInfo getAddonInfo(String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }

        return addonsCache.get(id);
    }

    public List<AddonInfo> getAllAddonInfos() {
        return new ArrayList<>(addonsCache.values());
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
                try {
                    doStart(addonInfo);
                } catch (Exception ex) {
                    LOG.error(ex.toString(), ex);
                    stop(addonInfo);
                }
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
            addonsCache.put(addonInfo.getId(), addonInfo);
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
        if (StrUtil.isBlank(id)) {
            return false;
        }
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
        if (addonInfo == null) {
            return false;
        }

        //执行插件的 uninstall 方法
        try {
            invokeAddonUnisntallMethod(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //删除所有插件文件
        try {
            clearAddonFiles(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //删除插件对应的数据库记录
        try {
            deleteDbRecord(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //清除插件的缓存信息
        try {
            clearAddonCache(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        return true;
    }

    private void deleteDbRecord(AddonInfo addonInfo) {
        OptionService optionService = Aop.get(OptionService.class);
        optionService.deleteByKey(ADDON_INSTALL_PREFFIX + addonInfo.getId());
    }

    private void clearAddonFiles(AddonInfo addonInfo) {
        FileUtils.deleteQuietly(new File(PathKit.getWebRootPath(), "addons/" + addonInfo.getId()));
        AddonUtil.forceDelete(addonInfo.buildJarFile());
    }

    private void invokeAddonUnisntallMethod(AddonInfo addonInfo) {
        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) {
            addon.onUninstall(addonInfo);
        }
    }

    private void clearAddonCache(AddonInfo addonInfo) {
        AddonUtil.clearAddonInfoCache(addonInfo.buildJarFile());
        addonInfo.setStatus(AddonInfo.STATUS_INIT);
        addonsCache.remove(addonInfo.getId());
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
        try {
            doStart(addonInfo);
            return true;
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            stop(addonInfo);
        }
        return false;
    }

    private void doStart(AddonInfo addonInfo) {

        //添加插件的 Controllers
        addControllers(addonInfo);

        //添加插件的 Handlers
        addHandlers(addonInfo);

        //添加插件的 指令
        addDirectives(addonInfo);

        //添加插件的拦截器
        addInterceptors(addonInfo);

        //添加微信消息插件
        addWechatAddons(addonInfo);

        //启动插件的数据库连接插件
        startActiveRecordPlugin(addonInfo);

        //执行插件 onStart() 方法
        invokeStartMethod(addonInfo);

        //构建 JPress 应用的 url 映射
        buildApplicationActionMapping();

        //设置插件的状态
        setAddonStatus(addonInfo);

    }


    private void setAddonStatus(AddonInfo addonInfo) {
        addonInfo.setStatus(AddonInfo.STATUS_START);
        OptionService optionService = Aop.get(OptionService.class);
        optionService.saveOrUpdate(ADDON_START_PREFFIX + addonInfo.getId(), "true");

    }

    private void buildApplicationActionMapping() {
        AddonControllerManager.buildActionMapping();
    }

    private void invokeStartMethod(AddonInfo addonInfo) {
        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) {
            addon.onStart(addonInfo);
        }
    }

    private void startActiveRecordPlugin(AddonInfo addonInfo) {
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
    }

    private void addWechatAddons(AddonInfo addonInfo) {
        List<Class<? extends WechatAddon>> wechatAddons = addonInfo.getWechatAddons();
        if (wechatAddons != null) {
            for (Class<? extends WechatAddon> c : wechatAddons) {
                WechatAddonConfig config = c.getAnnotation(WechatAddonConfig.class);
                WechatAddonInfo wechatAddon = new WechatAddonInfo(config, c);
                WechatAddonManager.me().addWechatAddon(wechatAddon);
            }
        }
    }

    private void addInterceptors(AddonInfo addonInfo) {
        List<Class<? extends Interceptor>> interceptorClasses = addonInfo.getInterceptors();
        if (interceptorClasses != null) {
            for (Class<? extends Interceptor> c : interceptorClasses)
                AddonInterceptorManager.addInterceptor(c);
        }
    }

    private void addDirectives(AddonInfo addonInfo) {
        List<Class<? extends Directive>> directives = addonInfo.getDirectives();
        if (directives != null) {
            for (Class<? extends Directive> c : directives) {
                JFinalDirective ann = c.getAnnotation(JFinalDirective.class);
                RenderManager.me().getEngine().addDirective(AnnotationUtil.get(ann.value()), c);
            }
        }
    }

    private void addHandlers(AddonInfo addonInfo) {
        List<Class<? extends Handler>> handlerClasses = addonInfo.getHandlers();
        if (handlerClasses != null) {
            for (Class<? extends Handler> c : handlerClasses)
                AddonHandlerManager.addHandler(c);
        }
    }

    private void addControllers(AddonInfo addonInfo) {
        List<Class<? extends Controller>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends Controller> c : controllerClasses)
                AddonControllerManager.addController(c, addonInfo.getId());
        }
    }


    public boolean stop(String id) {
        return stop(getAddonInfo(id));
    }


    public boolean stop(AddonInfo addonInfo) {

        //删除插件的所有Controller
        try {
            deleteControllers(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //删除插件的所有handler
        try {
            deleteHandlers(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //删除插件的所有指令
        try {
            deleteDirectives(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //删除插件的所有拦截器
        try {
            deleteInteceptors(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //移除所有的微信插件
        try {
            deleteWechatAddons(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //停止插件的数据库访问
        try {
            stopActiveRecordPlugin(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //移除所有的table数据
        try {
            removeModelsCache(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //移除插件的模板缓存
        try {
            removeTemplateCache(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //调用插件的 stop() 方法
        try {
            invokeAddonStopMethod(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //重建整个JPress应用的ActionMapping映射
        try {
            rebuildApplicationActionMapping();
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        //清除插件的状态，防止被重用
        try {
            deleteStartedFlag(addonInfo);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        return true;
    }

    private void deleteStartedFlag(AddonInfo addonInfo) {
        addonInfo.setStatus(AddonInfo.STATUS_INSTALL);
        OptionService optionService = Aop.get(OptionService.class);
        optionService.deleteByKey(ADDON_START_PREFFIX + addonInfo.getId());
    }

    private void rebuildApplicationActionMapping() {
        AddonControllerManager.buildActionMapping();
    }

    private void invokeAddonStopMethod(AddonInfo addonInfo) {
        Addon addon = Aop.get(addonInfo.getAddonClass());
        if (addon != null) addon.onStop(addonInfo);
    }

    private void removeTemplateCache(AddonInfo addonInfo) {
        // 清除模板引擎的 field 和 method 缓存
        // 否则可能会出现  object is not an instance of declaring class 的异常
        // https://gitee.com/fuhai/jpress/issues/IS5YQ
        try {
            RenderManager.me().getEngine().removeAllTemplateCache();

            Field fieldGetterCacheField = FieldKit.class.getDeclaredField("fieldGetterCache");
            fieldGetterCacheField.setAccessible(true);
            SyncWriteMap fieldGetterCacheMap = (SyncWriteMap) fieldGetterCacheField.get(null);
            fieldGetterCacheMap.clear();

            Field methodCacheField = MethodKit.class.getDeclaredField("methodCache");
            methodCacheField.setAccessible(true);
            SyncWriteMap methodCacheMap = (SyncWriteMap) methodCacheField.get(null);
            methodCacheMap.clear();

        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }
    }

    private void stopActiveRecordPlugin(AddonInfo addonInfo) {
        ActiveRecordPlugin arp = addonInfo.getArp();
        if (arp != null) {
            arp.stop();
        }
    }

    /**
     * 必须要移除所有的缓存，否则当插件卸载重新安装的时候，
     * 缓存里的可能还存在数据，由于可能是内存缓存，所有可能导致Class转化异常的问题
     * PS：每次新安装的插件，都是一个新的 Classloader
     *
     * @param addonInfo
     */
    private void removeModelsCache(AddonInfo addonInfo) {
        List<Class<? extends JbootModel>> modelClasses = addonInfo.getModels();
        if (modelClasses != null) {
            modelClasses.forEach(aClass -> {
                Table table = aClass.getAnnotation(Table.class);
                String tableName = AnnotationUtil.get(table.tableName());
                JbootModelConfig.getConfig().getCache().removeAll(tableName);
                Jboot.getCache().removeAll(tableName);
            });
        }
    }

    private void deleteWechatAddons(AddonInfo addonInfo) {
        List<Class<? extends WechatAddon>> wechatAddons = addonInfo.getWechatAddons();
        if (wechatAddons != null) {
            for (Class<? extends WechatAddon> c : wechatAddons) {
                WechatAddonConfig config = c.getAnnotation(WechatAddonConfig.class);
                WechatAddonManager.me().deleteWechatAddon(AnnotationUtil.get(config.id()));
            }
        }
    }

    private void deleteInteceptors(AddonInfo addonInfo) {
        List<Class<? extends Interceptor>> interceptorClasses = addonInfo.getInterceptors();
        if (interceptorClasses != null) {
            for (Class<? extends Interceptor> c : interceptorClasses)
                AddonInterceptorManager.deleteInterceptor(c);
        }
    }

    private void deleteDirectives(AddonInfo addonInfo) {
        List<Class<? extends Directive>> directives = addonInfo.getDirectives();
        if (directives != null) {
            for (Class<? extends Directive> c : directives) {
                JFinalDirective ann = c.getAnnotation(JFinalDirective.class);
                RenderManager.me().getEngine().removeDirective(AnnotationUtil.get(ann.value()));
            }
        }
    }

    private void deleteHandlers(AddonInfo addonInfo) {
        List<Class<? extends Handler>> handlerClasses = addonInfo.getHandlers();
        if (handlerClasses != null) {
            for (Class<? extends Handler> c : handlerClasses)
                AddonHandlerManager.deleteHandler(c);
        }
    }

    private void deleteControllers(AddonInfo addonInfo) {
        List<Class<? extends Controller>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends Controller> c : controllerClasses)
                AddonControllerManager.deleteController(c);
        }
    }


    public Ret upgrade(File newAddonFile, String oldAddonId) {
        AddonInfo oldAddon = AddonManager.me().getAddonInfo(oldAddonId);
        if (oldAddon == null) {
            return failRet("升级失败：无法读取旧的插件信息，可能该插件不存在。");
        }

        AddonInfo addon = AddonUtil.readSimpleAddonInfo(newAddonFile);
        if (addon == null || StrUtil.isBlank(addon.getId())) {
            return failRet("升级失败：无法读取新插件配置文件。");
        }

        if (!addon.getId().equals(oldAddonId)) {
            return failRet("升级失败：新插件的ID和旧插件不一致。");
        }

        if (addon.getVersionCode() <= oldAddon.getVersionCode()) {
            return failRet("升级失败：新插件的版本号必须大于已安装插件的版本号。");
        }

        boolean upgradeSuccess = false;
        try {
            upgradeSuccess = doUpgrade(newAddonFile, addon, oldAddon);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
            upgradeSuccess = false;
        } finally {
            if (!upgradeSuccess) {
                doUpgradeRollback(addon, oldAddon);
            }
        }

        return upgradeSuccess ? Ret.ok() : failRet("插件升级失败，请联系管理员。");
    }

    /**
     * 开始升级
     *
     * @param newAddon
     * @param oldAddon
     */
    private boolean doUpgrade(File newAddonFile, AddonInfo newAddon, AddonInfo oldAddon) throws IOException {

        //对已经安装的插件进行备份
        doBackupOldAddon(oldAddon);

        //解压缩新的资源文件
        doUnzipNewAddon(newAddonFile, newAddon);

        //获得已经进行 classloader 加载的 addon
        if (!doInvokeAddonUpgradeMethod(newAddon, oldAddon)) {
            return false;
        }

        //设置该插件的状态为已经安装的状态
        if (!doSetAddonStatus(newAddon.getId())) {
            return false;
        }

        //启动插件
//        start(newAddon.getId());

        return true;
    }

    private boolean doSetAddonStatus(String id) {

        AddonInfo addonInfo = addonsCache.get(id);
        addonInfo.setStatus(AddonInfo.STATUS_INSTALL);

        OptionService optionService = Aop.get(OptionService.class);
        return optionService.saveOrUpdate(ADDON_INSTALL_PREFFIX + id, "true") != null;
    }

    private boolean doInvokeAddonUpgradeMethod(AddonInfo newAddon, AddonInfo oldAddon) {

        AddonUtil.clearAddonInfoCache(newAddon.buildJarFile());
        AddonInfo addon = AddonUtil.readAddonInfo(newAddon.buildJarFile());

        AddonUpgrader upgrader = Aop.get(addon.getUpgraderClass());

        if (upgrader != null) {
            boolean success = false;
            try {
                success = upgrader.onUpgrade(oldAddon, addon);
            } finally {
                if (!success) {
                    upgrader.onRollback(oldAddon, addon);
                    return false;
                }
            }
        }

        addonsCache.put(addon.getId(), addon);
        return true;
    }

    private void doUnzipNewAddon(File newAddonFile, AddonInfo newAddon) throws IOException {
        File destAddonFile = newAddon.buildJarFile();
        FileUtils.moveFile(newAddonFile, destAddonFile);
        AddonUtil.unzipResources(newAddon);
    }

    private void doBackupOldAddon(AddonInfo oldAddon) throws IOException {

        //备份 资源文件
        String resPath = PathKit.getWebRootPath()
                + File.separator
                + "addons"
                + File.separator
                + oldAddon.getId();

        String resBakPath = resPath + "_bak";

        //清空之前的备份文件
        File resBakPathDir = new File(resBakPath);
        if (resBakPathDir.exists()) {
            FileUtils.deleteDirectory(resBakPathDir);
        }

        //备份资源文件
        FileUtils.moveDirectory(new File(resPath), resBakPathDir);

        File jarFile = oldAddon.buildJarFile();

        File bakJarFile = new File(jarFile.getAbsolutePath() + "_bak");
        if (bakJarFile.exists()) {
            FileUtils.deleteQuietly(bakJarFile);
        }

        //备份jar文件
        FileUtils.moveFile(jarFile, bakJarFile);
    }

    /**
     * 升级回滚
     *
     * @param addon
     * @param oldAddon
     */
    private void doUpgradeRollback(AddonInfo addon, AddonInfo oldAddon) {

        //删除刚刚升级安装的插件信息
        try {
            doDeleteNewAddon(addon);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }


        //恢复已经备份的插件信息
        try {
            doRollBackBackups(addon);
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }


        //重置插件信息
        try {
            doRestAddonInfo(oldAddon);
            doSetAddonStatus(oldAddon.getId());
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

    }

    private void doRestAddonInfo(AddonInfo addonInfo) {
        AddonUtil.clearAddonInfoCache(addonInfo.buildJarFile());
        AddonInfo addon = AddonUtil.readAddonInfo(addonInfo.buildJarFile());
        addonsCache.put(addon.getId(), addon);
    }

    private void doRollBackBackups(AddonInfo addon) {
        //备份 资源文件
        String resPath = PathKit.getWebRootPath()
                + File.separator
                + "addons"
                + File.separator
                + addon.getId();

        String resBakPath = resPath + "_bak";

        //恢复备份的资源文件
        try {
            FileUtils.moveDirectory(new File(resBakPath), new File(resPath));
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }

        //恢复备份的jar文件
        try {
            File bakJarFile = new File(addon.buildJarFile() + "_bak");
            FileUtils.moveFile(bakJarFile, addon.buildJarFile());
        } catch (IOException e) {
            LOG.error(e.toString(), e);
        }

    }

    private void doDeleteNewAddon(AddonInfo addon) {
        AddonUtil.clearAddonInfoCache(addon.buildJarFile());

        //删除已解压缩的资源文件
        FileUtils.deleteQuietly(new File(PathKit.getWebRootPath(), "addons/" + addon.getId()));

        //删除jar包
        AddonUtil.forceDelete(addon.buildJarFile());

        //删除插件列表缓存
        addonsCache.remove(addon.getId());
    }

    private Ret failRet(String msg) {
        return Ret.fail()
                .set("success", false)
                .setIfNotBlank("message", msg);
    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }
}
