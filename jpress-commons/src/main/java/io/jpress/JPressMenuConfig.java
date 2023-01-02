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
package io.jpress;

import io.jboot.Jboot;
import io.jboot.app.config.annotation.ConfigModel;

import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 菜单配置
 * @Package io.jpress
 */
@ConfigModel(prefix = "jpress.menu")
public class JPressMenuConfig {

    private boolean addonEnable = true;
    private boolean templateEnable = true;
    private boolean userEnable = true;
    private boolean wechatEnable = true;

    private Set<String> excludeActionKeys;


    public boolean isAddonEnable() {
        return addonEnable;
    }

    public void setAddonEnable(boolean addonEnable) {
        this.addonEnable = addonEnable;
    }

    public boolean isTemplateEnable() {
        return templateEnable;
    }

    public void setTemplateEnable(boolean templateEnable) {
        this.templateEnable = templateEnable;
    }

    public boolean isUserEnable() {
        return userEnable;
    }

    public void setUserEnable(boolean userEnable) {
        this.userEnable = userEnable;
    }

    public boolean isWechatEnable() {
        return wechatEnable;
    }

    public void setWechatEnable(boolean wechatEnable) {
        this.wechatEnable = wechatEnable;
    }

    public Set<String> getExcludeActionKeys() {
        return excludeActionKeys;
    }

    public void setExcludeActionKeys(Set<String> excludeActionKeys) {
        this.excludeActionKeys = excludeActionKeys;
    }

    public boolean isExcludeActionKey(String actionKey) {
        return excludeActionKeys != null && excludeActionKeys.contains(actionKey);
    }

    public static final JPressMenuConfig me = Jboot.config(JPressMenuConfig.class);
}
