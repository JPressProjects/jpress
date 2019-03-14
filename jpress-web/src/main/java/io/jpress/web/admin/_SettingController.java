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
package io.jpress.web.admin;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.web.base.AdminControllerBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/setting", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _SettingController extends AdminControllerBase {


    @AdminMenu(text = "常规", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 0)
    public void index() {
        render("setting/base.html");
    }

    @AdminMenu(text = "通信", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 9)
    public void connection() {
        render("setting/connection.html");
    }


    @AdminMenu(text = "接口", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 10)
    public void api() {
        render("setting/api.html");
    }

    @AdminMenu(text = "登录注册", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 32)
    public void reg() {
        render("setting/reg.html");
    }


    @AdminMenu(text = "网站加速", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 33)
    public void cdn() {
        render("setting/cdn.html");
    }


    @AdminMenu(text = "搜索优化", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 111)
    public void seo() {
        render("setting/seo.html");
    }

    @AdminMenu(text = "小工具箱", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 222)
    public void tools() {
        List<String> moduleIncludes = new ArrayList<>();
        List<ModuleListener> listeners = ModuleManager.me().getListeners();
        for (ModuleListener listener : listeners) {
            String path = listener.onRenderToolsBox(this);
            if (path == null) {
                continue;
            }

            if (path.startsWith("/")) {
                moduleIncludes.add(path);
            } else {
                moduleIncludes.add("/WEB-INF/views/admin/" + path);
            }
        }

        setAttr("moduleIncludes", moduleIncludes);
        render("setting/tools.html");
    }


}
