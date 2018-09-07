package io.jpress.web.admin;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.FileUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.*;
import io.jpress.service.UtmService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.service.PermissionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.admin.kits.PermissionKits;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/user")
public class _UserController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private UtmService utmService;

    @AdminMenu(text = "用户管理", groupId = JPressConstants.SYSTEM_MENU_USER, order = 0)
    public void index() {

        Page<User> page = userService.paginate(getPagePara(), 10);
        setAttr("page", page);

        render("user/list.html");
    }


    public void permissions() {
        List<Permission> permissions = permissionService.findAll();
        setAttr("permissionGroup", PermissionKits.groupPermission(permissions));
        render("user/user_permissions.html");
    }

    public void edit() {
        render("user/edit.html");
    }

    @AdminMenu(text = "角色", groupId = JPressConstants.SYSTEM_MENU_USER, order = 5)
    public void role() {
        List<Role> roles = roleService.findAll();
        setAttr("roles", roles);
        render("user/role.html");
    }

    public void rolePermissions() {
        List<Permission> permissions = permissionService.findAll();
        setAttr("permissionGroup", PermissionKits.groupPermission(permissions));
        render("user/role_permissions.html");
    }


    public void roleEdit() {
        long id = getParaToLong("id", 0l);
        if (id > 0) {
            setAttr("role", roleService.findById(id));
        }
        render("user/role_edit.html");
    }

    public void roleSave() {
        Role role = getModel(Role.class);
        roleService.saveOrUpdate(role);
        redirect("/admin/user/role");
    }


    @AdminMenu(text = "我的资料", groupId = JPressConstants.SYSTEM_MENU_USER)
    public void me() {
        User user = getLoginedUser().copy();
        setAttr("user", user);
        exeUtmAction();
        render(getRenderHtml());
    }


    public void detail() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);
        exeUtmAction();
        render(getRenderHtml());
    }


    private String getRenderHtml() {
        String action = getPara("action", "base");
        if ("base".equals(action)) return "user/detail.html";

        return "user/detail_" + action + ".html";
    }

    private void exeUtmAction() {
        String action = getPara("action", "base");
        if ("utm".equals(action)) {
            Page<Utm> page = utmService.paginate(getPagePara(), 10);
            setAttr("page", page);
        }
    }


    public void doSaveUser() {
        User user = getBean(User.class);
        userService.saveOrUpdate(user);
        renderJson(Ret.ok());
    }

    public void doUpdatePwd() {
        String oldPwd = getPara("oldPwd");
        String newPwd = getPara("newPwd");
        String confirmPwd = getPara("confirmPwd");

        renderJson(Ret.ok());
    }

    public void doSaveAvatar(String path, int x, int y, int w, int h) {
        String oldPath = PathKit.getWebRootPath() + path;

        //先进行图片缩放，保证图片和html的图片显示大小一致
        String zoomPath = AttachmentUtils.newAttachemnetFile(FileUtils.getSuffix(path)).getAbsolutePath();
        ImageUtils.zoom(500, oldPath, zoomPath); //500的值必须和 html图片的max-width值一样

        //进行剪切
        String newAvatarPath = AttachmentUtils.newAttachemnetFile(FileUtils.getSuffix(path)).getAbsolutePath();
        ImageUtils.crop(zoomPath, newAvatarPath, x, y, w, h);

        User loginedUser = getLoginedUser();
        loginedUser.setAvatar(FileUtils.removeRootPath(newAvatarPath));
        userService.saveOrUpdate(loginedUser);
        renderJson(Ret.ok());
    }

}
