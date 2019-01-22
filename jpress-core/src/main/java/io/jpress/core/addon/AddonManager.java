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
import com.jfinal.kit.PathKit;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jpress.core.addon.controller.AddonController;
import io.jpress.core.addon.controller.AddonControllerManager;
import io.jpress.core.addon.handler.AddonHandler;
import io.jpress.core.addon.handler.AddonHandlerManager;
import io.jpress.core.addon.interceptor.AddonInterceptor;
import io.jpress.core.addon.interceptor.AddonInterceptorManager;
import io.jpress.core.install.JPressInstaller;
import io.jpress.service.OptionService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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

    private static final AddonManager me = new AddonManager();

    public static AddonManager me() {
        return me;
    }

    private Set<AddonInfo> addonInfoList = new HashSet<>();

    public void init() {

        if (JPressInstaller.isInstalled() == false) {
            JPressInstaller.addListener(this);
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
                doStrat(addonInfo);
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
            AddonUtil.unzipResources(jarFile);
            addon.onInstall();
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
        addon.onUninstall();

        addonInfo.buildJarFile().delete();
        FileUtils.deleteQuietly(new File(PathKit.getWebRootPath(), "addon/" + addonInfo.getId()));

        addonInfo.setStatus(AddonInfo.STATUS_INIT);

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
     * 3、回调插件的 onStart()
     * 4、每次重启 JPress 都会调用 onStart()
     *
     * @param addonInfo
     * @return
     */
    public boolean start(AddonInfo addonInfo) {
        OptionService optionService = Aop.get(OptionService.class);
        optionService.saveOrUpdate("addonStart:" + addonInfo.getId(), "true");

        doStrat(addonInfo);

        return true;
    }

    private void doStrat(AddonInfo addonInfo) {
        List<Class<? extends AddonController>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends AddonController> c : controllerClasses)
                AddonControllerManager.addController(c, addonInfo);

            AddonControllerManager.buildActionMapping();
        }


        List<Class<? extends AddonHandler>> handlerClasses = addonInfo.getHandlers();
        if (handlerClasses != null) {
            for (Class<? extends AddonHandler> c : handlerClasses)
                AddonHandlerManager.addHandler(c);
        }

        List<Class<? extends AddonInterceptor>> interceptorClasses = addonInfo.getInterceptors();
        if (interceptorClasses != null) {
            for (Class<? extends AddonInterceptor> c : interceptorClasses)
                AddonInterceptorManager.addInterceptor(c);
        }

        Addon addon = Aop.get(addonInfo.getAddonClass());
        addon.onStart();

        addonInfo.setStatus(AddonInfo.STATUS_START);
    }


    public boolean stop(String id) {
        return stop(getAddonInfo(id));
    }


    public boolean stop(AddonInfo addonInfo) {


        OptionService optionService = Aop.get(OptionService.class);
        optionService.deleteByKey("addonStart:" + addonInfo.getId());

        List<Class<? extends AddonController>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends AddonController> c : controllerClasses)
                AddonControllerManager.deleteController(c);

            AddonControllerManager.buildActionMapping();
        }


        List<Class<? extends AddonHandler>> handlerClasses = addonInfo.getHandlers();
        if (handlerClasses != null) {
            for (Class<? extends AddonHandler> c : handlerClasses)
                AddonHandlerManager.deleteHandler(c);
        }

        List<Class<? extends AddonInterceptor>> interceptorClasses = addonInfo.getInterceptors();
        if (interceptorClasses != null) {
            for (Class<? extends AddonInterceptor> c : interceptorClasses)
                AddonInterceptorManager.deleteInterceptor(c);
        }

        Addon addon = Aop.get(addonInfo.getAddonClass());
        addon.onStop();

        addonInfo.setStatus(AddonInfo.STATUS_INSTALL);

        return true;

    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }
}
