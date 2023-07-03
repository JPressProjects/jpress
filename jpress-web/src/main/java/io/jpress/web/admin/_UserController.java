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
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
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
import io.jpress.commons.utils.SessionUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Permission;
import io.jpress.model.Role;
import io.jpress.model.User;
import io.jpress.service.PermissionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.service.UtmService;
import io.jpress.web.admin.kits.PermissionKits;
import io.jpress.web.base.AdminControllerBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/user", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _UserController extends AdminControllerBase {


    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private UtmService utmService;



    @AdminMenu(text = "用户管理", groupId = JPressConsts.SYSTEM_MENU_USER, order = 0)
    public void list() {

        Columns columns = Columns.create("status", getPara("status"));
        columns.likeAppendPercent("username", getTrimPara("username"));
        columns.likeAppendPercent("email", getTrimPara("email"));
        columns.likeAppendPercent("mobile", getTrimPara("mobile"));
        columns.eq("create_source", getPara("create_source"));

        Page<User> page = userService._paginate(getPagePara(), 10, columns, getParaToLong("group_id"));

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

        User dbUser = userService.findFirstByUsername(user.getUsername());
        if (dbUser != null) {
            renderJson(Ret.fail().set("message", "该用户名已经存在").set("errorCode", 10));
            return;
        }

        if (StrUtil.isNotBlank(user.getEmail())) {
            dbUser = userService.findFirstByEmail(user.getEmail());
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

        render("user/detail.html");
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
            @Form(name = "newPwd", message = "新密码不能为空"),
            @Form(name = "confirmPwd", message = "确认密码不能为空")
    })
    public void doUpdatePwd(long uid, String newPwd, String confirmPwd) {

        if (!newPwd.equals(confirmPwd)) {
            renderJson(Ret.fail().set("message", "两次出入密码不一致"));
            return;
        }

        User user = userService.findById(uid);
        if (user == null) {
            renderJson(Ret.fail().set("message", "该用户不存在"));
            return;
        }


        User loginUser = getLoginedUser();

        //当前登录用户如果不是超级管理员，不能修改超级管理员的密码
        if (!roleService.isSupperAdmin(loginUser.getId()) && roleService.isSupperAdmin(uid)){
            renderJson(Ret.fail().set("message", "您没有权限修改该用户密码"));
            return;
        }


        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + newPwd);

        user.setPassword(hashedPass);
        userService.update(user);

        //移除用户登录 session
        SessionUtils.forget(uid);

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

        String attachmentRoot = JPressConfig.me.getAttachmentRootOrWebRoot();

        String oldPath = attachmentRoot + path;

        //先进行图片缩放，保证图片和html的图片显示大小一致
        String zoomPath = AttachmentUtils.newAttachemnetFile(FileUtil.getSuffix(path)).getAbsolutePath();
        ImageUtils.zoom(500, oldPath, zoomPath); //500的值必须和 html图片的max-width值一样

        //进行剪切
        String newAvatarPath = AttachmentUtils.newAttachemnetFile(FileUtil.getSuffix(path)).getAbsolutePath();
        ImageUtils.crop(zoomPath, newAvatarPath, x, y, w, h);

        String newPath = FileUtil.removePrefix(newAvatarPath, attachmentRoot).replace("\\", "/");

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
    public void doDelByIds() {
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
