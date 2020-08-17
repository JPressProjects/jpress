/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author michael yang (fuhai999@gmail.com)
 * 参考 JarClassPath，用于解决插件在线安装后，JbootActionReporter 无法获取插件的类的问题
 */
public class AddonClassPath  implements ClassPath {

    Set<String> jarfileEntries;
    String jarfileURL;

    public AddonClassPath(String pathname) throws NotFoundException {
        JarFile jarfile = null;
        try {
            jarfile = new JarFile(pathname);
            jarfileEntries = new HashSet<>();
            for (JarEntry je: Collections.list(jarfile.entries())) {
                if (je.getName().endsWith(".class")) {
                    jarfileEntries.add(je.getName());
                }
            }
            jarfileURL = new File(pathname).getCanonicalFile()
                    .toURI().toURL().toString();
            return;
        } catch (IOException e) {}
        finally {
            if (null != jarfile) {
                try {
                    jarfile.close();
                } catch (IOException e) {}
            }
        }
        throw new NotFoundException(pathname);
    }

    @Override
    public InputStream openClassfile(String classname)
            throws NotFoundException
    {
        URL jarURL = find(classname);
        if (null != jarURL)
            try {
                if (ClassPool.cacheOpenedJarFile) {
                    return jarURL.openConnection().getInputStream();
                } else {
                    java.net.URLConnection con = jarURL.openConnection();
                    con.setUseCaches(false);
                    return con.getInputStream();
                }
            }
            catch (IOException e) {
                throw new NotFoundException("broken jar file?: "
                        + classname);
            }
        return null;
    }

    @Override
    public URL find(String classname) {
        String jarname = classname.replace('.', '/') + ".class";
        if (jarfileEntries.contains(jarname))
            try {
                return new URL(String.format("jar:%s!/%s", jarfileURL, jarname));
            }
            catch (MalformedURLException e) {}
        return null;            // not found
    }

    @Override
    public String toString() {
        return jarfileURL == null ? "<null>" : jarfileURL;
    }
}
