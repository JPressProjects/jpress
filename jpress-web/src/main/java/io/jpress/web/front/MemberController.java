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
package io.jpress.web.front;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.commons.pay.PayConfigUtil;
import io.jpress.commons.pay.PayStatus;
import io.jpress.model.Member;
import io.jpress.model.MemberGroup;
import io.jpress.model.PaymentRecord;
import io.jpress.service.MemberGroupService;
import io.jpress.service.MemberService;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;
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

    @Inject
    private MemberService memberService;


    /**
     * 购物车
     */
    public void index() {
        List<MemberGroup> memberGroups = memberGroupService.findUcenterList();
        List<Member> userMembers = memberService.findListByUserId(getLoginedUser().getId());

        for (MemberGroup group : memberGroups) {
            for (Member member : userMembers) {
                if (member.getGroupId().equals(group.getId())) {
                    group.put("member", member);
                }
            }
        }


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

    @EmptyValidate({
            @Form(name = "paytype", message = "支付方式不能为空"),
    })
    public void joining() {

        MemberGroup memberGroup = memberGroupService.findById(getPara("groupId"));
        if (memberGroup == null) {
           renderFailJson("该会员可能已经被管理员下架");
            return;
        }

        BigDecimal joinAmount = memberGroup.getPrice();
        BigDecimal limitedPrice = memberGroup.getLimitedPrice();
        if (limitedPrice != null && limitedPrice.subtract(joinAmount).intValue() <= 0) {
            joinAmount = limitedPrice;
        }

        if (joinAmount == null || joinAmount.compareTo(BigDecimal.ZERO) <= 0) {
            renderFailJson("该会员无法加入，因为加入金额小于0");
            return;
        }

        PaymentRecord payment = new PaymentRecord();
        payment.setProductTitle("加入会员");
        payment.setProductRelativeType("member_group");
        payment.setProductRelativeId(memberGroup.getId().toString());
        payment.setProductSummary(memberGroup.getSummary());

        payment.setTrxNo(StrUtil.uuid());
        payment.setTrxType(PaymentRecord.TRX_TYPE_MEMBER);
        payment.setTrxNonceStr(StrUtil.uuid());

        payment.setPayerUserId(getLoginedUser().getId());
        payment.setPayerName(getLoginedUser().getNickname());
        payment.setPayerFee(BigDecimal.ZERO);
        payment.setPayStatus(PayStatus.UNPAY.getStatus());//预支付

        payment.setOrderIp(getIPAddress());
        payment.setOrderRefererUrl(getReferer());

        payment.setPayAmount(joinAmount);
        payment.setPayType(getPara("paytype"));


        payment.setStatus(PaymentRecord.STATUS_PAY_PRE); //预支付

        PaymentRecordService paymentService = Aop.get(PaymentRecordService.class);
        paymentService.save(payment);

        renderJson(Ret.ok().set("gotoUrl", PayKit.buildPayUrl(payment.getPayType(), payment.getTrxNo())));
    }


}
