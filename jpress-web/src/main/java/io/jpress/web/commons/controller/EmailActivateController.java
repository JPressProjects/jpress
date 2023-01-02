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
package io.jpress.web.commons.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.User;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.UserControllerBase;
import io.jpress.web.commons.email.EmailSender;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/commons/emailactivate")
public class EmailActivateController extends UserControllerBase {


    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;


    public void index() {
        Long userId = getParaToLong("userId");
        if (userId == null) {
            renderJson(Ret.fail().set("message", "userId不能为空"));
            return;
        }

        // 如果当前用户不是超级管理员
        // 需要对传入的ID进行验证，只能自己给自己发送邮件
        if (roleService.isSupperAdmin(getLoginedUser().getId()) == false) {
            if (getLoginedUser().getId().equals(userId) == false) {
                renderJson(Ret.fail().set("message", "没有权限操作"));
                return;
            }
        }

        User user = userService.findById(userId);
        if (user == null) {
            renderJson(Ret.fail().set("message", "用户不存在或已经被删除"));
            return;
        }

        EmailSender.sendForEmailActivate(user);
        renderJson(Ret.ok().set("message", "激活邮件已经发送成功"));
    }


}
