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
package io.jpress.core.support.ehcache;


import io.jboot.app.config.JbootConfigManager;
import io.jboot.utils.StrUtil;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为什么需要 EhcacheManager ?
 * <p>
 * EhcacheManager 的主要作用是用于初始化 EhCache 的 CacheManager
 * 默认情况下，Ehcache 是通过 默认的（系统的） Classloader 加载数据的
 * 但是由于 JPress 内置了插件机制，插件的所有class都是通过插件自己的 Classloader 进行加载
 * 这样就会导致 EhCache 和 插件的 Classloader 不是通过一个 Classloader，当插件使用ehcache缓存的时候，
 * 就会导致 ClassNotFound 的异常出现
 * <p>
 * 所以，此类的主要作用，是对 EhCache Classloader 进行配置，保证能够加载到 插件自己的 Class
 * <p>
 * 另外：对于插件来说，每个插件必须使用自己的 Classloader，才能保证 插件在后台进行 安装、卸载、停止、启用的正常工作
 * 否则当用户卸载插件后重新安装，无法加载到新的Class（之前的Class还在内存里）
 */
public class EhcacheManager {

    private static EhcacheClassloader ehcacheClassloader = new EhcacheClassloader();

    public static void init() {

        Configuration config = ConfigurationFactory.parseConfiguration();
        config.setClassLoader(ehcacheClassloader);

        String maxBytesLocalHeap = JbootConfigManager.me().getConfigValue("jpress.ehcache.maxBytesLocalHeap");
        config.setMaxBytesLocalHeap(StrUtil.obtainDefaultIfBlank(maxBytesLocalHeap, "100M"));

        String maxBytesLocalDisk = JbootConfigManager.me().getConfigValue("jpress.ehcache.maxBytesLocalDisk");
        config.setMaxBytesLocalDisk(StrUtil.obtainDefaultIfBlank(maxBytesLocalDisk, "5G"));

        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setClassLoader(ehcacheClassloader);
        config.defaultCache(cacheConfiguration);

        CacheManager.create(config);
    }

    public static void addMapping(String className, ClassLoader classLoader) {
        ehcacheClassloader.addMapping(className, classLoader);
    }


    public static class EhcacheClassloader extends ClassLoader {

        private ClassLoader parent = EhcacheClassloader.class.getClassLoader();
        private Map<String, ClassLoader> classLoaderCache = null;


        public synchronized void addMapping(String className, ClassLoader classLoader) {
            if (classLoaderCache == null) {
                classLoaderCache = new ConcurrentHashMap<>();
            }
            classLoaderCache.put(className, classLoader);
        }


        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (classLoaderCache == null || classLoaderCache.isEmpty()) {
                return parent.loadClass(name);
            }
            ClassLoader c = classLoaderCache.get(name);
            if (c == null) {
                c = parent;
            }
            return c.loadClass(name);
        }
    }
}
