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

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.model.Role;
import io.jpress.model.User;
import io.jpress.model.Utm;
import io.jpress.service.*;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/user/detail", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _UserInfoController extends AdminControllerBase {


    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private UtmService utmService;


    public void index() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail.html");
    }

    public void role() {

        User user = userService.findById(getParaToLong());
        setAttr("user", user);

        List<Role> roles = roleService.findAll();
        setAttr("roles", roles);

        render("user/detail_role.html");
    }



    public void communication() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail_communication.html");
    }


    public void pwd() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail_pwd.html");
    }


    public void signature() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail_signature.html");
    }


    public void other() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail_other.html");
    }


    public void avatar() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail_avatar.html");
    }


    public void utm() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        Page<Utm> page = utmService._paginateByUserId(getPagePara(), 20, user.getId());
        setAttr("page", page);

        render("user/detail_utm.html");
    }


}
