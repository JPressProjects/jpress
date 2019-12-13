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
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.model.*;
import io.jpress.service.*;
import io.jpress.web.base.AdminControllerBase;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/user/detail", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _UserInfoController extends AdminControllerBase {

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
    private UserAmountStatementService amountStatementService;

    @Inject
    private UserTagService userTagService;


    public void index() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        render("user/detail.html");
    }

    public void role() {

        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        //用户没有权限，显示无权限页面
        if (!permissionService.hasPermission(getLoginedUser().getId(), USER_ROLE_EDIT_ACTION)) {
            renderErrorForNoPermission();
            return;
        }

        List<Role> roles = roleService.findAll();
        setAttr("roles", roles);

        render("user/detail_role.html");
    }


    public void tag(){
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        List<UserTag> userTags = userTagService.findListByUserId(uid);
        setAttr("userTags",userTags);

        List<UserTag> hotTags = userTagService.findHotList(50);
        setAttr("hotTags",hotTags);

        render("user/detail_tag.html");
    }



    public void finance() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        BigDecimal incomeAmount = amountStatementService.queryIncomeAmount(user.getId());
        BigDecimal payAmount = amountStatementService.queryPayAmount(user.getId());
        BigDecimal payoutAmount = amountStatementService.queryPayoutAmount(user.getId());

        setAttr("incomeAmount",incomeAmount);
        setAttr("payAmount",payAmount);
        setAttr("payoutAmount",payoutAmount);

        setAttr("userAmount",userService.queryUserAmount(user.getId()));
        setAttr("userAmountStatements",amountStatementService.findListByUserId(user.getId(),10));

        render("user/detail_finance.html");
    }



    public void member() {
        Long uid = getParaToLong();
        User user = userService.findById(uid);
        setAttr("user", user);

        List<Member> members = memberService.findListByUserId(user.getId());
        setAttr("members", members);

        render("user/detail_member.html");
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
