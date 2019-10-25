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
package io.jpress.web.front;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.pay.PayConfigUtil;
import io.jpress.model.MemberGroup;
import io.jpress.service.MemberGroupService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping(value = "/ucenter/member", viewPath = "/WEB-INF/views/ucenter")
public class MemberController extends UcenterControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private MemberGroupService memberGroupService;


    /**
     * 购物车
     */
    public void index() {
        List<MemberGroup> memberGroups = memberGroupService.findAll();
        setAttr("memberGroups", memberGroups);
        render("member/member_list.html");
    }

    public void detail() {
        MemberGroup memberGroup = memberGroupService.findById(getPara());
        setAttr("memberGroup", memberGroup);
        render("member/member_detail.html");
    }

    public void join() {
        MemberGroup memberGroup = memberGroupService.findById(getPara());
        PayConfigUtil.setConfigAttrs(this);
        setAttr("memberGroup", memberGroup);
        render("member/member_join.html");
    }


}
