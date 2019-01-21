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

import java.io.IOException;

/**
 * 插件管理器：安装、卸载、启用、停用
 *
 * 安装的动作：
 * 1、解压jar资源文件
 * 2、解压成功后，回调插件的 onInstall()
 * 3、回调成功后，标识该插件已经安装
 *
 * 卸载的动作：
 * 1、回调插件的 onUninstall()
 * 2、回调成功后删除jar文件以及资源文件
 * 3、删除安装、启动、停止等标识
 *
 * 启用插件：
 * 1、标识插件启用
 * 2、添加插件的 controller、handler、interceptor 到对应的管理器
 * 3、回调插件的 onStart()
 * 4、每次重启 JPress 都会调用 onStart()
 *
 * 停用插件：
 * 1、删除插件的 controller、handler、interceptor
 * 2、删除插件启用标识
 * 3、回调插件的 onStop()
 *
 *
 * 插件开发：
 * 1、插件的作者可以在 onInstall() 进行数据库表创建等工作
 * 2、插件的作者可以在 onUninstall() 进行数据库表删除和其他资源删除等工作
 */
public class AddonManager {

    private static final AddonManager me = new AddonManager();

    public static AddonManager me() {
        return me;
    }


    public boolean install(AddonInfo addonInfo) {


        try {
            AddonUtil.unzipResources(addonInfo.buildJarFile());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;

    }

    public boolean uninstall(AddonInfo addonInfo) {



        return true;

    }
}
