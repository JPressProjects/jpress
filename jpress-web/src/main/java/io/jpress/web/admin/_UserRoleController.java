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
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Permission;
import io.jpress.model.Role;
import io.jpress.service.*;
import io.jpress.web.admin.kits.PermissionKits;
import io.jpress.web.base.AdminControllerBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/user/role", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _UserRoleController extends AdminControllerBase {


    @Inject
    private RoleService roleService;

    @Inject
    private PermissionService permissionService;


    @AdminMenu(text = "角色", groupId = JPressConsts.SYSTEM_MENU_USER, order = 9)
    public void index() {
        List<Role> roles = roleService.findAll();
        setAttr("roles", roles);
        render("user/role.html");
    }

    public void permissions() {
        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }

        Role role = roleService.findById(id);
        setAttr("role", role);

        String type = getPara("type");


        List<Permission> permissions = type == null
                ? permissionService.findAll()
                : permissionService.findListByType(type);

        Map<String, List<Permission>> permissionGroup = PermissionKits.groupPermission(permissions);

        Map<String, Boolean> groupCheck = new HashMap();
        for (String groupKey : permissionGroup.keySet()) {
            List<Permission> permList = permissionGroup.get(groupKey);
            for (Permission permission : permList) {
                boolean hasPerm = roleService.hasPermission(role.getId(), permission.getId());
                if (!hasPerm) {
                    groupCheck.put(groupKey, false);
                    break;
                } else {
                    groupCheck.put(groupKey, true);
                }
            }
        }

        setAttr("groupCheck", groupCheck);
        setAttr("permissionGroup", permissionGroup);

        render("user/role_permissions.html");
    }

    public void edit() {
        Long id = getParaToLong();
        if (id != null) {
            setAttr("role", roleService.findById(id));
        }
        render("user/role_edit.html");
    }

    public void doSave() {
        Role role = getBean(Role.class);
        if (getParaToBoolean("issuper", false)) {
            role.setFlag(Role.ADMIN_FLAG);
        } else {
            role.setFlag(null);
        }

        roleService.saveOrUpdate(role);
        renderOkJson();
    }

    /**
     * 删除角色
     */
    public void doDel() {
        roleService.deleteById(getIdPara());
        renderOkJson();
    }


    /**
     * 批量删除角色
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(roleService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


}
