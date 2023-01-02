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

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConfig;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.SessionUtils;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.module.ModuleManager;
import io.jpress.model.User;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.handler.JPressHandler;
import io.jpress.web.interceptor.PermissionInterceptor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AdminController extends AdminControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Clear
    public void login() {

        if (!JPressHandler.getCurrentTarget().equals(JPressConfig.me.getAdminLoginPage())) {
            renderError(404);
            return;
        }

        setAttr("captchaEnable", JPressConfig.me.isAdminLoginCaptchaValidateEnable());
        setAttr("action", JPressConfig.me.getAdminLoginAction());
        render("login.html");
    }


    @Clear
    @EmptyValidate({
            @Form(name = "user", message = "账号不能为空"),
            @Form(name = "pwd", message = "密码不能为空"),
    })
    public void doLogin(String user, String pwd) {

        pwd = getOriginalPara("pwd");

        if (!JPressHandler.getCurrentTarget().equals(JPressConfig.me.getAdminLoginAction())) {
            renderError(404);
            return;
        }

        //必须使用验证码进行验证
        if (JPressConfig.me.isAdminLoginCaptchaValidateEnable()) {
            String captchaPara = getPara("captcha");
            if (StrUtil.isBlank(captchaPara)) {
                renderFailJson("验证码不能为空");
                return;
            }

            if (!validateCaptcha("captcha")) {
                renderFailJson("验证码不正确，请重新输入");
                return;
            }
        }

        if (StrUtil.isBlank(user) || StrUtil.isBlank(pwd)) {
            throw new RuntimeException("你当前的编辑器（idea 或者 eclipse）可能有问题，请参考文档：http://www.jfinal.com/doc/3-3 进行配置");
        }

        User loginUser = userService.findByUsernameOrEmail(user);
        if (loginUser == null) {
            renderJson(Ret.fail("message", "用户名不正确。"));
            return;
        }

        if (!roleService.hasAnyRole(loginUser.getId())) {
            renderJson(Ret.fail("message", "您没有登录的权限。"));
            return;
        }

        Ret ret = userService.doValidateUserPwd(loginUser, pwd);

        if (ret.isOk()) {
            SessionUtils.record(loginUser.getId());
            CookieUtil.put(this, JPressConsts.COOKIE_UID, loginUser.getId());
        }

        renderJson(ret);
    }

    // 必须清除PermissionInterceptor，
    // 防止在没有授权的情况下，用户无法退出的问题
    @Clear(PermissionInterceptor.class)
    public void logout() {
        CookieUtil.remove(this, JPressConsts.COOKIE_UID);
        redirect("/");
    }

    public void index() {
        List<ModuleDashboardBoxInfo> leftBoxes = new ArrayList<>();
        List<ModuleDashboardBoxInfo> rightBoxes = new ArrayList<>();

        List<ModuleListener> listeners = ModuleManager.me().getListeners();
        for (ModuleListener listener : listeners) {
            String path = listener.onRenderDashboardBox(this);
            if (StrUtil.isBlank(path)) {
                continue;
            }

           ModuleDashboardBoxInfo info = new ModuleDashboardBoxInfo(path);
            if (info.isLeft()){
                leftBoxes.add(info);
            }else {
                rightBoxes.add(info);
            }
        }

        leftBoxes.sort(Comparator.comparingInt(o -> o.index));
        rightBoxes.sort(Comparator.comparingInt(o -> o.index));

        List<String> leftBoxPaths = leftBoxes.stream().map(moduleDashboardBoxInfo -> moduleDashboardBoxInfo.path).collect(Collectors.toList());
        List<String> rightBoxPaths = rightBoxes.stream().map(moduleDashboardBoxInfo -> moduleDashboardBoxInfo.path).collect(Collectors.toList());

        setAttr("leftBoxPaths", leftBoxPaths);
        setAttr("rightBoxPaths", rightBoxPaths);

        render("index.html");
    }


    static class ModuleDashboardBoxInfo {
        String path;
        int position = 0; // 0 left 1 right
        int index = 0;

        public ModuleDashboardBoxInfo(String path) {
            String[] pathStrings = path.split(":");
            if (pathStrings.length == 1) {
                setPath(pathStrings[0]);
            } else if (pathStrings.length == 2) {
                setPath(pathStrings[0]);
                String positionOrIndex = pathStrings[1].trim();

                if (StrUtil.isNumeric(positionOrIndex)) {
                    this.index = Integer.valueOf(positionOrIndex);
                } else {
                    this.position = "left".equalsIgnoreCase(positionOrIndex) ? 0 : 1;
                }
            } else if (pathStrings.length == 3) {
                setPath(pathStrings[0]);
                this.position = "left".equalsIgnoreCase(pathStrings[1].trim()) ? 0 : 1;
                this.index = Integer.valueOf(pathStrings[2].trim());
            }
        }

        private void setPath(String path){
            path = path.trim();
            if (!path.startsWith("/")) {
              path = "/WEB-INF/views/admin/" + path;
            }
            this.path = path;
        }

        public boolean isLeft() {
            return position == 0;
        }

    }
}
