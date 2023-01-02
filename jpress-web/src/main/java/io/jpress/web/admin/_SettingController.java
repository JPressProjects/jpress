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
package io.jpress.web.admin;

import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.email.Email;
import io.jpress.commons.email.SimpleEmailSender;
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

    @EmptyValidate({
            @Form(name = "email", message = "邮箱地址不能为空")
    })
    public void testEmail() {
        String emailAddr = getPara("email");
        if (!StrUtil.isEmail(emailAddr)) {
            renderFailJson("您输入的邮箱地址有误。");
            return;
        }

        Email email = Email.create();

        email.subject("这是一封来至 JPress 的测试邮件");
        email.content("恭喜您，收到此邮件，证明您在 JPress 后台配置的邮件可用。");
        email.to(emailAddr);

        SimpleEmailSender ses = new SimpleEmailSender();
        if (!ses.isEnable()) {
            renderFailJson("您未开启邮件功能，无法发送。");
            return;
        }

        if (!ses.isConfigOk()) {
            renderFailJson("未配置正确，smtp 或 用户名 或 密码 为空。");
            return;
        }

        if (!ses.send(email)) {
            renderFailJson("未配置正确，smtp 或 用户名 或 密码 错误。");
            return;
        }

        renderOkJson();
    }


    @AdminMenu(text = "接口", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 10)
    public void api() {
        render("setting/api.html");
    }


    @AdminMenu(text = "SEO", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 15)
    public void seo() {
        render("setting/seo.html");
    }


    @AdminMenu(text = "登录注册", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 32)
    public void reg() {
        render("setting/reg.html");
    }


    @AdminMenu(text = "网站加速", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 33)
    public void cdn() {
        render("setting/cdn.html");
    }

    @AdminMenu(text = "垃圾过滤", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 44)
    public void filter() throws Exception {
        render("setting/filter.html");
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

    //@AdminMenu(text = "图标", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 223)
    public void icons() {
        render("setting/icons.html");
    }

}
