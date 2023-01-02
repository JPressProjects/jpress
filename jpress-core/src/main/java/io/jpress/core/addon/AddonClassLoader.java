/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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


import com.google.common.collect.Lists;
import com.jfinal.aop.AopManager;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.Controller;
import com.jfinal.handler.Handler;
import com.jfinal.log.Log;
import com.jfinal.template.Directive;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.aop.annotation.Bean;
import io.jboot.aop.annotation.BeanExclude;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.mq.JbootmqMessageListener;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ArrayUtil;
import io.jpress.core.addon.annotation.GlobalInterceptor;
import io.jpress.core.wechat.WechatAddon;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class AddonClassLoader extends URLClassLoader {

    private static final Log LOG = Log.getLog(AddonClassLoader.class);

    private AddonInfo addonInfo;
    private List<String> classNameList;

    public AddonClassLoader(AddonInfo addonInfo) throws Exception {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());

        URL addPath = addonInfo.buildJarFile().toURI().toURL();

        this.addURL(addPath);
        this.addonInfo = addonInfo;
        this.classNameList = new ArrayList<>();
        this.initClassNameList();
    }

    public List<String> getClassNameList() {
        return classNameList;
    }

    private void initClassNameList() throws IOException {
        Enumeration<JarEntry> entries = new JarFile(addonInfo.buildJarFile()).entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                classNameList.add(className);
            }
        }
    }

    public void load() {
        for (String className : classNameList) {
            try {
                Class loadedClass = null;
                try {
                        loadedClass = loadClass(className);
                }catch (NoClassDefFoundError ex){
                    //可能会出现类存在，但是无法连接的情况
                    //比如 com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory
                    //虽然使用了 fastjson，但是却未引用 Retrofit2 的依赖就会是 Retrofit2ConverterFactory 导致无法 load
                    continue;
                }



                Bean bean = (Bean) loadedClass.getDeclaredAnnotation(Bean.class);
                if (bean != null) {
                    initBeanMapping(loadedClass);
                }

                // controllers
                if (Controller.class.isAssignableFrom(loadedClass)) {
                    addonInfo.addController(loadedClass);
                }
                // interceptors
                else if (Interceptor.class.isAssignableFrom(loadedClass)) {
                    if (loadedClass.getAnnotation(GlobalInterceptor.class) != null) {
                        addonInfo.addInterceptor(loadedClass);
                    }
                }
                // interceptorBuilders
                else if (InterceptorBuilder.class.isAssignableFrom(loadedClass)){
                    if (loadedClass.getAnnotation(AutoLoad.class) != null){
                        addonInfo.addInterceptorBuilder(loadedClass);
                    }
                }
                // handlers
                else if (Handler.class.isAssignableFrom(loadedClass)) {
                    addonInfo.addHandler(loadedClass);
                }
                // models
                else if (JbootModel.class.isAssignableFrom(loadedClass)) {
                    addonInfo.addModel(loadedClass);
                }
                // directives
                else if (Directive.class.isAssignableFrom(loadedClass)) {
                    addonInfo.addDirective(loadedClass);
                }
                // wechatAddons
                else if (WechatAddon.class.isAssignableFrom(loadedClass)) {
                    addonInfo.addWechatAddon(loadedClass);
                }
                // addonClass
                else if (Addon.class.isAssignableFrom(loadedClass)) {
                    addonInfo.setAddonClass(loadedClass);
                }
                // upgraderClass
                else if (AddonUpgrader.class.isAssignableFrom(loadedClass)) {
                    addonInfo.setUpgraderClass(loadedClass);
                }

            } catch (Exception ex) {
                LOG.error(ex.toString(), ex);
            }
        }
    }



    /**
     * 初始化 @Bean 注解的映射关系
     */
    private void initBeanMapping(Class<?> implClass) {

        Class<?>[] interfaceClasses = implClass.getInterfaces();

        if (interfaceClasses == null || interfaceClasses.length == 0) {
            return;
        }

        Class<?>[] excludes = buildExcludeClasses(implClass);

        for (Class interfaceClass : interfaceClasses) {
            if (!inExcludes(interfaceClass, excludes)) {
                AopManager.me().addMapping(interfaceClass, implClass);
            }
        }
    }


    private static Class<?>[] DEFAULT_EXCLUDES = new Class[]{JbootEventListener.class, JbootmqMessageListener.class, Serializable.class};

    private Class<?>[] buildExcludeClasses(Class<?> implClass) {
        BeanExclude beanExclude = implClass.getAnnotation(BeanExclude.class);

        //对某些系统的类 进行排除，例如：Serializable 等
        return beanExclude == null
                ? DEFAULT_EXCLUDES
                : ArrayUtil.concat(DEFAULT_EXCLUDES, beanExclude.value());
    }


    private boolean inExcludes(Class<?> interfaceClass, Class<?>[] excludes) {
        for (Class<?> ex : excludes) {
            if (ex.isAssignableFrom(interfaceClass)) {
                return true;
            }
        }
        return false;
    }

    static final List<?> SUPPORT_NATIVE_SUFFIXES = Lists.newArrayList(".so", ".dylib", ".dll");

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream superStream = super.getResourceAsStream(name);
        int dotLastIndex = name.lastIndexOf(".");
        if (dotLastIndex > -1) {
            String suffix = name.substring(dotLastIndex);
            boolean isSupport = SUPPORT_NATIVE_SUFFIXES.contains(suffix);
            if (superStream == null && isSupport) {
                try {
                    ZipEntry zipEntry = new ZipEntry(name);
                    return new JarFile(addonInfo.buildJarFile()).getInputStream(zipEntry);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return superStream;
    }

}
