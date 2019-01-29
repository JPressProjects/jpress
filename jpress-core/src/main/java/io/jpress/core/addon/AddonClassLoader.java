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


import com.jfinal.aop.Interceptor;
import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonClassLoader extends URLClassLoader {

    private static final Log LOG = Log.getLog(AddonClassLoader.class);

    private AddonInfo addonInfo;

    public AddonClassLoader(AddonInfo addonInfo) {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
        this.addonInfo = addonInfo;
    }


    public void load() {
        try {

            File jarFile = addonInfo.buildJarFile();
            addURL(jarFile.toURI().toURL());
            Enumeration<JarEntry> entries = new JarFile(addonInfo.buildJarFile()).entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                    try {
                        Class loadedClass = loadClass(className);
                        // controllers
                        if (Controller.class.isAssignableFrom(loadedClass)) {
                            addonInfo.addController(loadedClass);
                        }
                        // interceptors
                        else if (Interceptor.class.isAssignableFrom(loadedClass)) {
                            addonInfo.addInterceptor(loadedClass);
                        }
                        // handlers
                        else if (Handler.class.isAssignableFrom(loadedClass)) {
                            addonInfo.addHandler(loadedClass);
                        }
                        // addonClass
                        else if (Addon.class.isAssignableFrom(loadedClass)) {
                            addonInfo.setAddonClass(loadedClass);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.toString(), e);
        }

    }
}
