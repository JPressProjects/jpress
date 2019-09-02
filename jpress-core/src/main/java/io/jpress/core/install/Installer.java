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
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.event.JbootEventManager;

import java.io.File;

public class Installer {

    public static final String INSTALL_EVENT = "jpress_install_ok";

    private static Boolean installed = null;

    public static boolean notInstall() {
        return !isInstalled();
    }

    public static boolean isInstalled() {
        if (installed == null) {
            init();
        }
        return installed;
    }

    private static void init() {
        File lockFile = new File(PathKit.getRootClassPath(), "install.lock");
        boolean lockFileOk = lockFile.exists() && lockFile.isFile();

        File propertieFile = new File(PathKit.getRootClassPath(), "jboot.properties");
        boolean propertieFileOk = propertieFile.exists() && propertieFile.isFile();

        installed = lockFileOk && propertieFileOk;

    }

    public static void setInstalled(boolean installed) {
        Installer.installed = installed;
    }

    public static void addListener(JbootEventListener eventListener) {
        JbootEventManager.me().registerListener(eventListener, false, INSTALL_EVENT);
    }

    public static void notifyAllListeners(){
        JbootEventManager.me().pulish(new JbootEvent(INSTALL_EVENT,null));
    }

}
