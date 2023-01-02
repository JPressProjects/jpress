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
package io.jpress.core.wechat;


import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.ClassUtil;

import java.io.Serializable;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.wechat
 */
public class WechatAddonInfo implements Serializable {

    private String id;
    private String title;
    private String description;
    private String author;
    private String authorWebsite;
    private String version;
    private Class<? extends WechatAddon> addonClazz;

    private int versionCode;

    public WechatAddonInfo() {
    }

    public WechatAddonInfo(WechatAddonConfig config, Class<? extends WechatAddon> addonClass) {
        this.id = AnnotationUtil.get(config.id());
        this.author = AnnotationUtil.get(config.author());
        this.authorWebsite = AnnotationUtil.get(config.authorWebsite());
        this.description = AnnotationUtil.get(config.description());
        this.addonClazz = addonClass;
        this.title = AnnotationUtil.get(config.title());
        this.version = AnnotationUtil.get(config.version());
        this.versionCode = config.versionCode();
    }

    public WechatAddonInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Class<? extends WechatAddon> getAddonClazz() {
        return addonClazz;
    }

    public void setAddonClazz(Class<? extends WechatAddon> addonClazz) {
        this.addonClazz = addonClazz;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    private WechatAddon addon;

    public WechatAddon getAddon() {
        if (addon == null) {
            synchronized (this) {
                if (addon == null) {
                    addon = ClassUtil.newInstance(addonClazz);
                }
            }
        }
        return addon;
    }

    public boolean isEnable() {
        return WechatAddonManager.me().isEnable(getId());
    }
}
