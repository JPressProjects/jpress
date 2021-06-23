/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.MemberGroup;
import io.jpress.model.MemberJoinedRecord;
import io.jpress.service.*;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/user/mgroup", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _UserMemberGroupController extends AdminControllerBase {

    @Inject
    private MemberGroupService memberGroupService;
    @Inject
    private MemberJoinedRecordService memberJoinedRecordService;



    @AdminMenu(text = "会员组", groupId = JPressConsts.SYSTEM_MENU_USER, order = 4)
    public void index() {
        List<MemberGroup> memberGroups = memberGroupService.findAll();
        setAttr("memberGroups", memberGroups);
        render("user/mgroup.html");
    }


    public void joined() {
        Page<MemberJoinedRecord> page = memberJoinedRecordService.paginateByGroupId(getPagePara(), 20, getParaToLong());
        setAttr("page", page);
        setAttr("group", memberGroupService.findById(getPara()));
        render("user/mgroupjoined.html");
    }

    public void edit() {
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
    public void doSave() {
        MemberGroup memberGroup = getModel(MemberGroup.class, "group");
        memberGroupService.saveOrUpdate(memberGroup);
        renderOkJson();
    }

    public void doDel() {
        memberGroupService.deleteById(getIdPara());
        renderOkJson();
    }


    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        for (String id : idsSet) {
            memberGroupService.deleteById(id);
        }
        renderOkJson();
    }


}
