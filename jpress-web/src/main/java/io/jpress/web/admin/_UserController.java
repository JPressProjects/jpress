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
import com.jfinal.core.ActionKey;
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
import io.jpress.model.UserTag;
import io.jpress.service.*;
import io.jpress.web.admin.kits.PermissionKits;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.email.AdminMessageSender;
import org.apache.commons.lang3.ArrayUtils;

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


    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private UtmService utmService;


    @Inject
    private UserTagService userTagService;


    @AdminMenu(text = "用户管理", groupId = JPressConsts.SYSTEM_MENU_USER, order = 0)
    public void list() {

        Columns columns = Columns.create("status", getPara("status"));
        columns.likeAppendPercent("username", getTrimPara("username"));
        columns.likeAppendPercent("email", getTrimPara("email"));
        columns.likeAppendPercent("mobile", getTrimPara("mobile"));
        columns.eq("create_source", getPara("create_source"));

        Page<User> page = userService._paginate(getPagePara(), 10, columns, getParaToLong("group_id"), getPara("tag"));

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

    @AdminMenu(text = "用户标签", groupId = JPressConsts.SYSTEM_MENU_USER, order = 1)
    public void tag() {

        Page<UserTag> page = userTagService.paginateByColumns(getPagePara(), 10, Columns.EMPTY, "id desc");
        setAttr("page", page);

        setAttr("tag", userTagService.findById(getPara()));

        render("user/tag_list.html");
    }


    @EmptyValidate({
            @Form(name = "tag.title", message = "标签名称不能为空"),
    })
    public void doTagSave() {
        UserTag tag = getModel(UserTag.class, "tag");
        tag.setSlug(tag.getTitle());

        String slug = tag.getSlug();
        if (slug == null || slug.contains("-") || StrUtil.isNumeric(slug)) {
            renderJson(Ret.fail("message", "固定连接不能以数字结尾"));
            return;
        }

        Object id = userTagService.saveOrUpdate(tag);
//        categoryService.doUpdateArticleCount(category.getId());


        renderOkJson();
    }


    public void doTagDel() {
        userTagService.deleteById(getPara());
        renderOkJson();
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




    @AdminMenu(text = "发消息", groupId = JPressConsts.SYSTEM_MENU_USER, order = 5)
    public void sendMsg() {
        List<UserTag> hotTags = userTagService.findHotList(50);
        setAttr("hotTags", hotTags);
        render("user/msg_email.html");
    }

    public void doSendEmail() {
        Long[] tagIds = getTagIds(getParaValues("userTags"));

        List<User> users = userService.findListByTagIds(Columns.create(), tagIds);
        String title = getPara("title");
        String content = getCleanedOriginalPara("content");
        String cc = getPara("cc");

        renderJson(AdminMessageSender.sendEmail(title, content, cc, users));
    }

    @ActionKey("/admin/user/sendMsg/wechat")
    public void sendWechatMsg() {
        List<UserTag> hotTags = userTagService.findHotList(50);
        setAttr("hotTags", hotTags);
        render("user/msg_wechat.html");
    }

    public void doSendWechat() {
        Long[] tagIds = getTagIds(getParaValues("userTags"));

        List<User> users = userService.findListByTagIds(Columns.create(), tagIds);
        String cc = getPara("cc");

        String templateId = getPara("templateId");
        String url = getPara("url");
        String first = getPara("first");
        String remark = getPara("remark");
        String keyword1 = getPara("keyword1");
        String keyword2 = getPara("keyword2");
        String keyword3 = getPara("keyword3");
        String keyword4 = getPara("keyword4");


        renderJson(AdminMessageSender.sendWechat(templateId, url, first, remark, keyword1, keyword2, keyword3, keyword4, users, cc));
    }


    @ActionKey("/admin/user/sendMsg/sms")
    public void sendSmsMsg() {
        List<UserTag> hotTags = userTagService.findHotList(50);
        setAttr("hotTags", hotTags);
        render("user/msg_sms.html");
    }


    public void doSendSms() {
        Long[] tagIds = getTagIds(getParaValues("userTags"));

        List<User> users = userService.findListByTagIds(Columns.create(), tagIds);
        String cc = getPara("cc");

        String smsTemplate = getPara("sms_template");
        String smsSign = getPara("sms_sign");


        renderJson(AdminMessageSender.sendSms(smsTemplate, smsSign, cc, users));
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


    public void doUpdateUserTags() {
        Long[] tagIds = getTagIds(getParaValues("tag"));
        userTagService.doUpdateTags(getParaToLong("userId"), tagIds);
        renderOkJson();
    }

    private Long[] getTagIds(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        Set<String> tagset = new HashSet<>();
        for (String tag : tags) {
            tagset.addAll(StrUtil.splitToSet(tag,","));
        }

        List<UserTag> userTags = userTagService.findOrCreateByTagString(tagset.toArray(new String[0]));
        long[] ids = userTags.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
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
