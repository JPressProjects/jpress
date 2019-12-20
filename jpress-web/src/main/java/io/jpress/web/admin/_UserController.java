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
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.*;
import io.jpress.service.*;
import io.jpress.web.admin.kits.PermissionKits;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.email.AdminMessageSender;
import org.apache.commons.lang.time.DateUtils;
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

    private static final String USER_ROLE_EDIT_ACTION = "/admin/user/roleEdit";

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private UtmService utmService;

    @Inject
    private MemberService memberService;

    @Inject
    private MemberGroupService memberGroupService;

    @Inject
    private MemberJoinedRecordService memberJoinedRecordService;

    @Inject
    private UserTagService userTagService;


    @AdminMenu(text = "用户管理", groupId = JPressConsts.SYSTEM_MENU_USER, order = 0)
    public void index() {

        Columns columns = Columns.create("status", getPara("status"));
        columns.likeAppendPercent("username", getPara("username"));
//        columns.likeAppendPercent("nickname", getPara("username"));
        columns.likeAppendPercent("email", getPara("email"));
        columns.likeAppendPercent("mobile", getPara("mobile"));
        columns.eq("create_source", getPara("create_source"));


        List<MemberGroup> memberGroups = memberGroupService.findAll();
        setAttr("memberGroups", memberGroups);

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
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
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


    public void memberedit() {
        List<MemberGroup> groups = memberGroupService.findNormalList();
        setAttr("groups", groups);

        setAttr("member", memberService.findById(getPara("id")));


        render("user/member_edit.html");
    }


    public void memberRenewal() {
        Member member = memberService.findById(getPara("id"));
        setAttr("member", memberService.findById(getPara("id")));
        setAttr("group", memberGroupService.findById(member.getGroupId()));


        render("user/member_renewal.html");
    }


    @EmptyValidate({
            @Form(name = "member.duetime", message = "到期时间不能为空")
    })
    public void doMemberSave() {
        Member member = getModel(Member.class);

        Member existModel = memberService.findByGroupIdAndUserId(member.getGroupId(), member.getUserId());
        if (existModel != null && !existModel.getId().equals(member.getId())) {
            renderFailJson("用户已经加入该会员");
            return;
        }

        MemberGroup group = memberGroupService.findById(member.getGroupId());
        if (group == null || !group.isNormal()) {
            renderFailJson("该会员组不存在或已经被禁用。");
            return;
        }

        if (member.getId() == null) {
            MemberJoinedRecord joinedRecord = new MemberJoinedRecord();
            joinedRecord.setUserId(member.getUserId());
            joinedRecord.setGroupId(member.getGroupId());
            joinedRecord.setGroupName(group.getName());
            joinedRecord.setJoinCount(1);
            joinedRecord.setJoinType(member.getSource());
            joinedRecord.setCreated(new Date());
            joinedRecord.setJoinFrom(MemberJoinedRecord.JOIN_FROM_ADMIN);

            if (Member.SOURCE_BUY.equals(member.getSource())) {
                joinedRecord.setJoinPrice(group.getPrice());
            }

            if (memberService.saveOrUpdate(member) == null) {
                renderFailJson();
                return;
            }
        }

        memberService.saveOrUpdate(member);
        renderOkJson();
    }


    /**
     * 会员续期
     */
    public void doMemberRenewal() {
        MemberJoinedRecord joinedRecord = getModel(MemberJoinedRecord.class, "", true);
        joinedRecord.setJoinFrom(MemberJoinedRecord.JOIN_FROM_ADMIN);
        joinedRecord.setJoinCount(1);


        Member member = memberService.findByGroupIdAndUserId(joinedRecord.getGroupId(), joinedRecord.getUserId());
        if (member == null) {
            renderFailJson();
            return;
        }


        Date oldDuetime = member.getDuetime();

        //如果该会员之前有记录，但是会员早就到期了，重新续费应该按现在时间开始计算
        if (oldDuetime.getTime() < System.currentTimeMillis()) {
            oldDuetime = new Date();
        }

        member.setDuetime(DateUtils.addDays(oldDuetime, joinedRecord.getValidTerm()));
        member.setModified(new Date());

        memberJoinedRecordService.save(joinedRecord);
        memberService.update(member);

        renderOkJson();
    }


    public void doMemberDel() {
        memberService.deleteById(getPara("id"));
        renderOkJson();
    }


    @AdminMenu(text = "会员组", groupId = JPressConsts.SYSTEM_MENU_USER, order = 4)
    public void mgroup() {
        List<MemberGroup> memberGroups = memberGroupService.findAll();
        setAttr("memberGroups", memberGroups);
        render("user/mgroup.html");
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
        String content = getPara("content");
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


    public void mgroupjoined() {
        Page<MemberJoinedRecord> page = memberJoinedRecordService.paginateByGroupId(getPagePara(), 20, getParaToLong());
        setAttr("page", page);
        setAttr("group", memberGroupService.findById(getPara()));
        render("user/mgroupjoined.html");
    }

    public void mgroupEdit() {
        Long id = getParaToLong();
        if (id != null) {
            setAttr("group", memberGroupService.findById(id));
        }
        render("user/mgroup_edit.html");
    }


    @EmptyValidate({
            @Form(name = "group.name", message = "会员名称不能为空"),
            @Form(name = "group.title", message = "会员标题不能为空"),
            @Form(name = "group.price", message = "会员加入费用不能为空"),
            @Form(name = "group.valid_term", message = "会员购买有效期不能为空"),
    })
    public void doMgroupSave() {
        MemberGroup memberGroup = getModel(MemberGroup.class, "group");
        memberGroupService.saveOrUpdate(memberGroup);
        renderOkJson();
    }

    public void doMgroupDel() {
        memberGroupService.deleteById(getIdPara());
        renderOkJson();
    }


    @EmptyValidate(@Form(name = "ids"))
    public void doMgroupDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        for (String id : idsSet) {
            memberGroupService.deleteById(id);
        }
        renderOkJson();
    }


    @AdminMenu(text = "角色", groupId = JPressConsts.SYSTEM_MENU_USER, order = 9)
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

        List<UserTag> userTags = userTagService.findOrCreateByTagString(tags);
        long[] ids = userTags.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }


    @EmptyValidate({
            @Form(name = "newPwd", message = "新密码不能为空"),
            @Form(name = "confirmPwd", message = "确认密码不能为空")
    })
    public void doUpdatePwd(long uid, String oldPwd, String newPwd, String confirmPwd) {

        User user = userService.findById(uid);
        if (user == null) {
            renderJson(Ret.fail().set("message", "该用户不存在"));
            return;
        }

        //超级管理员可以修改任何人的密码
        if (!roleService.isSupperAdmin(getLoginedUser().getId())) {
            if (StrUtil.isBlank(oldPwd)) {
                renderJson(Ret.fail().set("message", "旧密码不能为空"));
                return;
            }

            if (userService.doValidateUserPwd(user, oldPwd).isFail()) {
                renderJson(Ret.fail().set("message", "密码错误"));
                return;
            }
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
