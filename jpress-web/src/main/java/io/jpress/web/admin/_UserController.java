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

import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConfig;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Permission;
import io.jpress.model.Role;
import io.jpress.model.User;
import io.jpress.model.Utm;
import io.jpress.service.PermissionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.service.UtmService;
import io.jpress.web.admin.kits.PermissionKits;
import io.jpress.web.base.AdminControllerBase;

import java.io.File;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/user", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _UserController extends AdminControllerBase {

    private static final String USER_ROLE_EDIT_ACTION = "/admin/user/roleEdit";

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private UtmService utmService;

    @AdminMenu(text = "用户管理", groupId = JPressConsts.SYSTEM_MENU_USER, order = 0)
    public void index() {

        Page<User> page = userService._paginate(getPagePara(), 10,
                getPara("username"),
                getPara("email"),
                getPara("status"));

        int lockedCount = userService.findCountByStatus(User.STATUS_LOCK);
        int regCount = userService.findCountByStatus(User.STATUS_REG);
        int okCount = userService.findCountByStatus(User.STATUS_OK);

        setAttr("lockedCount", lockedCount);
        setAttr("regCount", regCount);
        setAttr("okCount", okCount);
        setAttr("totalCount", lockedCount + regCount + okCount);

        setAttr("page", page);

        List<Role> roles = roleService.findAll();
        setAttr("roles", roles);

        render("user/list.html");
    }


    public void permissions() {
        User user = userService.findById(getParaToLong());
        setAttr("user", user);
        List<Permission> permissions = permissionService.findAll();
        setAttr("permissionGroup", PermissionKits.groupPermission(permissions));
        render("user/user_permissions.html");
    }

    public void edit() {
        render("user/edit.html");
    }

    /**
     * 新增用户
     */
    public void doAdd() {

        String pwd = getPara("newPwd");
        String confirmPwd = getPara("confirmPwd");
        User user = getBean(User.class);

        if (StrUtil.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtil.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        User dbUser = userService.findFistByUsername(user.getUsername());
        if (dbUser != null) {
            renderJson(Ret.fail().set("message", "该用户名已经存在").set("errorCode", 10));
            return;
        }

        if (StrUtil.isNotBlank(user.getEmail())) {
            dbUser = userService.findFistByEmail(user.getEmail());
            if (dbUser != null) {
                renderJson(Ret.fail().set("message", "邮箱已经存在了").set("errorCode", 11));
                return;
            }
        }

        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        user.setSalt(salt);
        user.setPassword(hashedPass);
        user.setCreated(new Date());
        user.setStatus(User.STATUS_OK);
        user.setCreateSource(User.SOURCE_ADMIN_CREATE);

        userService.save(user);

        renderOkJson();
    }

    @AdminMenu(text = "角色", groupId = JPressConsts.SYSTEM_MENU_USER, order = 5)
    public void role() {
        List<Role> roles = roleService.findAll();
        setAttr("roles", roles);
        render("user/role.html");
    }

    public void rolePermissions() {
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

    @ActionKey(USER_ROLE_EDIT_ACTION)
    public void roleEdit() {
        Long id = getParaToLong();
        if (id != null) {
            setAttr("role", roleService.findById(id));
        }
        render("user/role_edit.html");
    }

    public void doRoleSave() {
        Role role = getBean(Role.class);
        if (getParaToBoolean("issuper", false)) {
            role.setFlag(Role.ADMIN_FLAG);
        } else {
            role.setFlag(null);
        }

        roleService.saveOrUpdate(role);
        redirect("/admin/user/role");
    }

    /**
     * 删除角色
     */
    public void doRoleDel() {
        roleService.deleteById(getIdPara());
        renderOkJson();
    }


    /**
     * 批量删除角色
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doRoleDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(roleService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    public void doDelRolePermission(long roleId, long permissionId) {
        roleService.delPermission(roleId, permissionId);
        renderOkJson();
    }


    public void doAddRolePermission(long roleId, long permissionId) {
        roleService.addPermission(roleId, permissionId);
        renderOkJson();
    }


    @AdminMenu(text = "我的资料", groupId = JPressConsts.SYSTEM_MENU_USER)
    public void me() {
        User user = getLoginedUser().copy();
        setAttr("user", user);
        if (exeOtherAction(user)) {
            render(getRenderHtml());
        }
    }


    public void detail() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);
        exeOtherAction(user);
        if (exeOtherAction(user)) {
            render(getRenderHtml());
        }
    }


    private String getRenderHtml() {
        String action = getPara("action", "base");
        if ("base".equals(action)) return "user/detail.html";

        return "user/detail_" + action + ".html";
    }

    private boolean exeOtherAction(User user) {
        String action = getPara("action", "base");
        if ("utm".equals(action)) {
            Page<Utm> page = utmService._paginateByUserId(getPagePara(), 20, user.getId());
            setAttr("page", page);
        }

        if ("role".equals(action)) {

            //不是超级管理员，不让修改用户角色
            if (permissionService.hasPermission(getLoginedUser().getId(), USER_ROLE_EDIT_ACTION) == false) {
                renderErrorForNoPermission();
                return false;
            }

            List<Role> roles = roleService.findAll();
            setAttr("roles", roles);
        }

        return true;
    }


    public void doSaveUser() {
        User user = getBean(User.class);

        //自己修改自己资料的时候，需要移除安全字段
        //修改其他人的资料的时候，可以不用修改
        if (getLoginedUser().getId().equals(user.getId())) {
            user.keepUpdateSafe();
        }

        userService.saveOrUpdate(user);
        renderOkJson();
    }


    public void doUpdateUserRoles() {

        Long userId = getParaToLong("userId");

        if (getLoginedUser().getId().equals(userId)) {
            renderJson(Ret.fail().set("message", "不能修改自己的角色"));
            return;
        }

        Long[] roleIds = getParaValuesToLong("roleId");
        roleService.doResetUserRoles(userId, roleIds);
        renderOkJson();
    }


    @EmptyValidate({
            @Form(name = "oldPwd", message = "旧不能为空"),
            @Form(name = "newPwd", message = "新密码不能为空"),
            @Form(name = "confirmPwd", message = "确认密码不能为空")
    })
    public void doUpdatePwd(long uid, String oldPwd, String newPwd, String confirmPwd) {

        User user = userService.findById(uid);
        if (user == null) {
            renderJson(Ret.fail().set("message", "该用户不存在"));
            return;
        }

        if (userService.doValidateUserPwd(user, oldPwd).isFail()) {
            renderJson(Ret.fail().set("message", "密码错误"));
            return;
        }

        if (newPwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次出入密码不一致"));
            return;
        }

        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + newPwd);

        user.setPassword(hashedPass);
        userService.update(user);

        renderOkJson();
    }

    @EmptyValidate({
            @Form(name = "path", message = "请先选择图片")
    })
    public void doSaveAvatar(String path, Long uid, int x, int y, int w, int h) {
        User user = userService.findById(uid);
        if (user == null) {
            renderFailJson();
            return;
        }

        String attachmentRoot = StrUtil.isNotBlank(JPressConfig.me.getAttachmentRoot())
                ? JPressConfig.me.getAttachmentRoot()
                : PathKit.getWebRootPath();

        String oldPath = attachmentRoot + path;

        //先进行图片缩放，保证图片和html的图片显示大小一致
        String zoomPath = AttachmentUtils.newAttachemnetFile(FileUtil.getSuffix(path)).getAbsolutePath();
        ImageUtils.zoom(500, oldPath, zoomPath); //500的值必须和 html图片的max-width值一样

        //进行剪切
        String newAvatarPath = AttachmentUtils.newAttachemnetFile(FileUtil.getSuffix(path)).getAbsolutePath();
        ImageUtils.crop(zoomPath, newAvatarPath, x, y, w, h);

        String newPath = FileUtil.removePrefix(newAvatarPath, attachmentRoot);
        AliyunOssUtils.upload(newPath, new File(newAvatarPath));

        user.setAvatar(newPath);
        userService.saveOrUpdate(user);
        renderOkJson();
    }


    /**
     * 删除用户
     */
    public void doUserDel() {
        if (getLoginedUser().getId().equals(getIdPara())) {
            renderJson(Ret.fail().set("message", "不能删除自己"));
            return;
        }
        userService.deleteById(getIdPara());
        renderOkJson();
    }

    /**
     * 删除用户
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doUserDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        if (idsSet.contains(getLoginedUser().getId().toString())) {
            renderJson(Ret.fail().set("message", "删除的用户不能包含自己"));
            return;
        }
        render(userService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 删除角色
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doChangeRoleByIds() {
        Set<String> idsSet = getParaSet("ids");
        Long roleId = getParaToLong("roleId");
        if (roleId == null || roleId <= 0) {
            renderFailJson();
            return;
        }
        render(roleService.doChangeRoleByIds(roleId, idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 修改评论状态
     */
    public void doUserStatusChange(Long id, String status) {
        if (getLoginedUser().getId().equals(id)) {
            renderJson(Ret.fail().set("message", "不能修改自己的状态"));
            return;
        }
        render(userService.doChangeStatus(id, status) ? OK : FAIL);
    }

    public void doAddGroupRolePermission(long roleId, String groupId) {
        List<Long> permIds = new ArrayList<Long>();
        List<Permission> permissionList = permissionService.findListByNode(groupId.replace("...", ""));
        for (Permission permission : permissionList) {
            //先清空再添加
            if (!roleService.hasPermission(roleId, permission.getId())) {
                roleService.addPermission(roleId, permission.getId());
            }
            permIds.add(permission.getId());
        }
        renderJson(Ret.ok().set("permissionIds", permIds));
    }

    public void doDelGroupRolePermission(long roleId, String groupId) {
        List<Long> permIds = new ArrayList<Long>();
        List<Permission> permissionList = permissionService.findListByNode(groupId.replace("...", ""));
        for (Permission permission : permissionList) {
            roleService.delPermission(roleId, permission.getId());
            permIds.add(permission.getId());
        }
        renderJson(Ret.ok().set("permissionIds", permIds));
    }
}
