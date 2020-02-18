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
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountPayout;
import io.jpress.model.UserAmountStatement;
import io.jpress.service.*;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.express.ExpressQuerierFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/finance", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FinanceController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_FinanceController.class);

    @Inject
    private PaymentRecordService paymentService;

    @Inject
    private UserOrderItemService orderItemService;

    @Inject
    private UserService userService;

    @Inject
    private UserAmountPayoutService payoutService;

    @Inject
    private UserAmountStatementService statementService;


    @AdminMenu(text = "支付记录", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 3)
    public void paylist() {
        Columns columns = Columns.create();
        columns.likeAppendPercent("trx_no", getPara("ns"));
        columns.likeAppendPercent("product_title", getPara("pt"));
        columns.eq("status", getPara("status"));

        Page<PaymentRecord> page = paymentService.paginate(getPagePara(), 20, columns);
        setAttr("page", page);

        long successCount = paymentService.findCountByColumns(Columns.create("status", PaymentRecord.STATUS_PAY_SUCCESS));
        long prepayCount = paymentService.findCountByColumns(Columns.create("status", PaymentRecord.STATUS_PAY_PRE));
        long failCount = paymentService.findCountByColumns(Columns.create("status", PaymentRecord.STATUS_PAY_FAILURE));

        setAttr("successCount", successCount);
        setAttr("prepayCount", prepayCount);
        setAttr("failCount", failCount);
        setAttr("totalCount", successCount + prepayCount + failCount);

        render("finance/paylist.html");
    }


    @AdminMenu(text = "提现管理", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 4)
    public void payout() {
        Page<UserAmountPayout> page = payoutService.paginateByColumns(getPagePara(), 10, Columns.create("status", getPara("status")), "id desc");
        userService.join(page, "user_id");
        setAttr("page", page);

        long totalCount = payoutService.findCountByColumns(Columns.EMPTY);
        long payingCount = payoutService.findCountByColumns(Columns.create("status", UserAmountPayout.STATUS_APPLYING));
        long refuseCount = payoutService.findCountByColumns(Columns.create("status", UserAmountPayout.STATUS_REFUSE));
        long successCount = payoutService.findCountByColumns(Columns.create("status", UserAmountPayout.STATUS_SUCCESS));

        setAttr("totalCount", totalCount);
        setAttr("payingCount", payingCount);
        setAttr("refuseCount", refuseCount);
        setAttr("successCount", successCount);

        render("finance/payout.html");

    }

    public void payoutdetail() {
        UserAmountPayout payout = payoutService.findById(getPara());

        setAttr("payout", payout);
        //modified by jializheng 20200218 取提现申请人的余额信息，而非当前登录用户（管理员）
        setAttr("userAmount", userService.queryUserAmount(payout.getUserId()));

        render("finance/payoutdetail.html");
    }


    public void payoutprocess() {

        UserAmountPayout payout = payoutService.findById(getPara());

        setAttr("payout", payout);
        render("finance/layer_payout_process.html");
    }

    @EmptyValidate({
            @Form(name = "amount", message = "打款金额不能为空")
    })
    public void doPayoutProcess() {
        UserAmountPayout payout = payoutService.findById(getPara("id"));

        //modified by jializheng 20200218 取提现申请人的余额信息，而非当前登录用户（管理员）
        BigDecimal userAmount = userService.queryUserAmount(payout.getUserId());
        if (userAmount == null || userAmount.compareTo(payout.getAmount()) < 0) {
            renderFailJson("用户余额不足，无法提现。");
            return;
        }

        BigDecimal amount = new BigDecimal(getPara("amount"));
        BigDecimal shouldPayAmount = payout.getAmount().subtract(payout.getFee());
        if (amount == null || amount.compareTo(shouldPayAmount) != 0) {
            renderFailJson("打款金额输入错误，实际应该给客户打款金额为：" + new DecimalFormat("0.00").format(shouldPayAmount) + " 元");
            return;
        }

        Db.tx(() -> {
            payout.setStatus(UserAmountPayout.STATUS_SUCCESS);
            payout.setPaySuccessProof(getPara("proof"));
            if (!payoutService.update(payout)) {
                return false;
            }

            //生成提现用户的流水信息
            UserAmountStatement statement = new UserAmountStatement();
            statement.setUserId(payout.getUserId());
            statement.setAction(UserAmountStatement.ACTION_PAYOUT);
            statement.setActionDesc("用户提现");
            statement.setActionName("用户提现");
            statement.setActionRelativeType("user_amount_payout");
            statement.setActionRelativeId(payout.getId());
            statement.setOldAmount(userAmount);
            statement.setChangeAmount(BigDecimal.ZERO.subtract(payout.getAmount()));
            statement.setNewAmount(userAmount.subtract(payout.getAmount()));

            if (statementService.save(statement) == null) {
                return false;
            }

            if (userService.updateUserAmount(payout.getUserId()
                    , userAmount
                    , BigDecimal.ZERO.subtract(payout.getAmount()))) {
                return false;
            }

            return true;
        });
        renderOkJson();
    }


    public void payoutrefuse() {

        UserAmountPayout payout = payoutService.findById(getPara());

        setAttr("payout", payout);
        render("finance/layer_payout_refuse.html");
    }

    public void doPayoutRefuse() {
        UserAmountPayout payout = payoutService.findById(getPara("id"));
        payout.setStatus(UserAmountPayout.STATUS_REFUSE);
        payout.setFeedback(getPara("feedback"));

        payoutService.update(payout);
        renderOkJson();
    }


    public void payUpdate() {
        PaymentRecord payment = paymentService.findById(getPara());
        render404If(payment == null);

        setAttr("payment", payment);
        render("finance/layer_payupdate.html");
    }

    public void doPayUpdate() {
        PaymentRecord payment = getModel(PaymentRecord.class, "payment");
        payment.keep("id", "pay_status", "pay_success_amount", "pay_success_time", "pay_success_proof", "pay_success_remarks");

        PaymentRecord dbPayment = paymentService.findById(payment.getId());
        if (dbPayment.getPayAmount().compareTo(payment.getPaySuccessAmount()) != 0) {
            renderFailJson("入账失败，入账金额和应付款金额不一致。");
            return;
        }

        if (dbPayment.isPaySuccess()) {
            renderFailJson("该支付已经支付成功。");
            return;
        }

        payment.setStatus(PaymentRecord.STATUS_PAY_SUCCESS);
        payment.setPayCompleteTime(new Date());

        if (paymentService.update(payment)) {
            paymentService.notifySuccess(payment.getId());
        }

        renderOkJson();
    }

    /**
     * 支付记录详情
     */
    public void payDetail() {
        PaymentRecord payment = paymentService.findById(getPara());
        render404If(payment == null);

        setAttr("payment", payment);
        render("finance/layer_paydetail.html");
    }


    @AdminMenu(text = "基础设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 9)
    public void setting() {
        setAttr("querierNames", ExpressQuerierFactory.getQuerierNames());
        render("finance/setting_base.html");
    }

    @AdminMenu(text = "收款设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 19)
    public void setting_pay() {
        setAttr("querierNames", ExpressQuerierFactory.getQuerierNames());
        render("finance/setting_pay.html");
    }

    @AdminMenu(text = "通知设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 29)
    public void setting_notify() {
        setAttr("querierNames", ExpressQuerierFactory.getQuerierNames());
        render("finance/setting_notify.html");
    }


}
