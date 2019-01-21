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


import com.jfinal.log.Log;
import io.jpress.core.addon.controller.AddonController;
import io.jpress.core.addon.handler.AddonHandler;
import io.jpress.core.addon.interceptor.AddonInterceptor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AddonClassLoader extends URLClassLoader {

    private static final Log log = Log.getLog(AddonClassLoader.class);
    private AddonInfo addonInfo;

    public AddonClassLoader(AddonInfo addonInfo) {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
        this.addonInfo = addonInfo;
    }

    public void init() {
        File jarFile = new File(addonInfo.getJarPath());
        try {
            addURL(jarFile.toURI().toURL());
        } catch (MalformedURLException e) {
            log.error("AddonClassLoader init error", e);
        }
    }

    public void doLoad(JarFile jarfile) {

        Enumeration<JarEntry> entries = jarfile.entries();

        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                try {
                    Class loadedClass = loadClass(className);

                    if (AddonController.class.isAssignableFrom(loadedClass)) {
                        addonInfo.addController(loadedClass);
                    }

                    if (AddonInterceptor.class.isAssignableFrom(loadedClass)) {
                        addonInfo.addInterceptor(loadedClass);
                    }

                    if (AddonHandler.class.isAssignableFrom(loadedClass)) {
                        addonInfo.addHandler(loadedClass);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
