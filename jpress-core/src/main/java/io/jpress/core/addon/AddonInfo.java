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
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.template.Directive;
import io.jboot.db.annotation.Table;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jpress.core.wechat.WechatAddon;
import io.jpress.core.wechat.WechatAddonConfig;

import java.io.File;
import java.io.Serializable;
import java.util.*;

public class AddonInfo implements Serializable {

    public static final int STATUS_INIT = 0;
    public static final int STATUS_INSTALL = 1;
    public static final int STATUS_START = 2;

    private String id;
    private String title;
    private String description;
    private String author;
    private String authorWebsite;
    private String version;
    private int versionCode;

    private Class<? extends Addon> addonClass;
    private Class<? extends AddonUpgrader> upgraderClass;
    private int status = STATUS_INIT;

    private List<Class<? extends Controller>> controllers;
    private List<Class<? extends Interceptor>> interceptors;
    private List<Class<? extends Handler>> handlers;
    private List<Class<? extends JbootModel>> models;
    private List<Class<? extends Directive>> directives;
    private List<Class<? extends WechatAddon>> wechatAddons;

    private ActiveRecordPlugin arp;
    private Map<String, String> config;

    private String readmeText;
    private String changeLogText;

    public AddonInfo() {

    }

    public AddonInfo(Properties properties) {
        this.id = properties.getProperty("id");
        this.title = properties.getProperty("title");
        this.description = properties.getProperty("description");
        this.author = properties.getProperty("author");
        this.authorWebsite = properties.getProperty("authorWebsite");
        this.version = StrUtil.obtainDefaultIfBlank(properties.getProperty("version"), "v1.0.0");
        this.versionCode = Integer.valueOf(properties.getProperty("versionCode", "1"));
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Class<? extends Addon> getAddonClass() {
        return addonClass;
    }

    public void setAddonClass(Class<? extends Addon> addonClass) {
        this.addonClass = addonClass;
    }

    public Class<? extends AddonUpgrader> getUpgraderClass() {
        return upgraderClass;
    }

    public void setUpgraderClass(Class<? extends AddonUpgrader> upgraderClass) {
        this.upgraderClass = upgraderClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorWebsite() {
        return authorWebsite;
    }

    public void setAuthorWebsite(String authorWebsite) {
        this.authorWebsite = authorWebsite;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public AddonInfo addConfig(String key, String value) {
        if (this.config == null) {
            this.config = new HashMap<>();
        }

        this.config.put(key, value);
        return this;
    }

    public boolean isInstall() {
        return status > STATUS_INIT;
    }

    public boolean isStarted() {
        return status > STATUS_INSTALL;
    }

    public void addController(Class<? extends Controller> clazz) {
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        controllers.add(clazz);
    }

    public List<Class<? extends Controller>> getControllers() {
        return controllers;
    }

    public void setControllers(List<Class<? extends Controller>> controllers) {
        this.controllers = controllers;
    }


    public void addInterceptor(Class<? extends Interceptor> clazz) {
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(clazz);
    }

    public List<Class<? extends Interceptor>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Class<? extends Interceptor>> interceptors) {
        this.interceptors = interceptors;
    }

    public void addHandler(Class<? extends Handler> clazz) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        handlers.add(clazz);
    }

    public List<Class<? extends Handler>> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Class<? extends Handler>> handlers) {
        this.handlers = handlers;
    }


    public void addModel(Class<? extends JbootModel> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            return;
        }
        if (models == null) {
            models = new ArrayList<>();
        }
        models.add(clazz);
    }

    public List<Class<? extends JbootModel>> getModels() {
        return models;
    }

    public void setModels(List<Class<? extends JbootModel>> models) {
        this.models = models;
    }


    public void addDirective(Class<? extends Directive> clazz) {
        JFinalDirective directive = clazz.getAnnotation(JFinalDirective.class);
        if (directive == null) {
            return;
        }
        if (directives == null) {
            directives = new ArrayList<>();
        }
        directives.add(clazz);
    }

    public List<Class<? extends Directive>> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Class<? extends Directive>> directives) {
        this.directives = directives;
    }

    public void addWechatAddon(Class<? extends WechatAddon> clazz) {
        WechatAddonConfig directive = clazz.getAnnotation(WechatAddonConfig.class);
        if (directive == null) {
            return;
        }
        if (wechatAddons == null) {
            wechatAddons = new ArrayList<>();
        }
        wechatAddons.add(clazz);
    }


    public List<Class<? extends WechatAddon>> getWechatAddons() {
        return wechatAddons;
    }

    public void setWechatAddons(List<Class<? extends WechatAddon>> wechatAddons) {
        this.wechatAddons = wechatAddons;
    }

    public ActiveRecordPlugin getArp() {
        return arp;
    }

    public ActiveRecordPlugin getOrCreateArp() {
        if (arp == null) {
            arp = AddonUtil.createRecordPlugin(this);
        }
        return arp;
    }

    public void setArp(ActiveRecordPlugin arp) {
        this.arp = arp;
    }


    public String getReadmeText() {
        return readmeText;
    }

    public void setReadmeText(String readmeText) {
        this.readmeText = readmeText;
    }

    public String getChangeLogText() {
        return changeLogText;
    }

    public void setChangeLogText(String changeLogText) {
        this.changeLogText = changeLogText;
    }

    public File buildJarFile() {

        String webRoot = PathKit.getWebRootPath();

        StringBuilder fileName = new StringBuilder(webRoot);
        fileName.append(File.separator);
        fileName.append("WEB-INF");
        fileName.append(File.separator);
        fileName.append("addons");
        fileName.append(File.separator);
        fileName.append(getId());
        fileName.append(".jar");

        return new File(fileName.toString());
    }

    public Addon getAddon() {
        return addonClass == null ? null : Aop.get(addonClass);
    }

    public AddonUpgrader getAddonUpgrader() {
        return upgraderClass == null ? null : Aop.get(upgraderClass);
    }


}
