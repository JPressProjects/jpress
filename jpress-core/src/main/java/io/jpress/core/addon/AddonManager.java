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
import io.jpress.core.addon.controller.AddonController;
import io.jpress.core.addon.controller.AddonControllerManager;
import io.jpress.core.addon.handler.AddonHandler;
import io.jpress.core.addon.handler.AddonHandlerManager;
import io.jpress.core.addon.interceptor.AddonInterceptor;
import io.jpress.core.addon.interceptor.AddonInterceptorManager;
import io.jpress.service.OptionService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
public class AddonManager {

    private static final AddonManager me = new AddonManager();

    public static AddonManager me() {
        return me;
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
        Addon addon = Aop.get(addonInfo.getAddonClass());

        try {
            AddonUtil.unzipResources(jarFile);
            addon.onInstall();
        } catch (IOException e) {
            e.printStackTrace();
        }

        OptionService optionService = Aop.get(OptionService.class);
        return optionService.saveOrUpdate("addonInstall:" + addonInfo.getId(), "true") != null;

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

        OptionService optionService = Aop.get(OptionService.class);
        return optionService.deleteByKey("addonInstall:" + addonInfo.getId());

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

        List<Class<? extends AddonController>> controllerClasses = addonInfo.getControllers();
        if (controllerClasses != null) {
            for (Class<? extends AddonController> c : controllerClasses)
                AddonControllerManager.addController(c, addonInfo);
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

        return true;
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

        return true;

    }
}
