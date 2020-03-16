package io.jpress.web.front;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressOptions;
import io.jpress.commons.pay.PayConfigUtil;
import io.jpress.commons.pay.PayStatus;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountPayout;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserAmountPayoutService;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;


@RequestMapping(value = "/ucenter/finance/amount", viewPath = "/WEB-INF/views/ucenter/finance")
public class FinanceController extends UcenterControllerBase {


    @Inject
    private UserService userService;

    @Inject
    private UserAmountStatementService amountStatementService;

    @Inject
    private UserAmountPayoutService payoutService;


    /**
     * 用户余额信息
     */
    public void index() {
        BigDecimal incomeAmount = amountStatementService.queryIncomeAmount(getLoginedUser().getId());
        BigDecimal payAmount = amountStatementService.queryPayAmount(getLoginedUser().getId());
        BigDecimal payoutAmount = amountStatementService.queryPayoutAmount(getLoginedUser().getId());

        setAttr("incomeAmount", incomeAmount);
        setAttr("payAmount", payAmount);
        setAttr("payoutAmount", payoutAmount);

        setAttr("userAmount", userService.queryUserAmount(getLoginedUser().getId()));
        setAttr("userAmountStatements", amountStatementService.findListByUserId(getLoginedUser().getId(), 10));
        render("amount.html");
    }

    public void payout() {
        Page<UserAmountPayout> page = payoutService.paginateByUserId(getPagePara(), 10, getLoginedUser().getId(), getParaToInt("status"));
        setAttr("page", page);

        long totalCount = payoutService.findCountByColumns(Columns.create("user_id", getLoginedUser().getId()));
        long payingCount = payoutService.findCountByColumns(Columns.create("user_id", getLoginedUser().getId()).eq("status", UserAmountPayout.STATUS_APPLYING));
        long refuseCount = payoutService.findCountByColumns(Columns.create("user_id", getLoginedUser().getId()).eq("status", UserAmountPayout.STATUS_REFUSE));
        long successCount = payoutService.findCountByColumns(Columns.create("user_id", getLoginedUser().getId()).eq("status", UserAmountPayout.STATUS_SUCCESS));

        setAttr("totalCount", totalCount);
        setAttr("payingCount", payingCount);
        setAttr("refuseCount", refuseCount);
        setAttr("successCount", successCount);

        render("payout.html");
    }


    public void payoutsubmit() {
        setAttr("userAmount", userService.queryUserAmount(getLoginedUser().getId()));
        render("payoutsubmit.html");
    }

    public void payoutdetail() {

        UserAmountPayout payout = payoutService.findById(getPara());
        render404If(notLoginedUserModel(payout));

        setAttr("payout", payout);
        setAttr("userAmount", userService.queryUserAmount(getLoginedUser().getId()));

        render("payoutdetail.html");
    }


    /**
     * 提交提现申请
     */
    @EmptyValidate({
            @Form(name = "payoutAmount", message = "提现金额不能为空"),
            @Form(name = "realName", message = "真实姓名不能为空"),
            @Form(name = "idcard", message = "身份证账号不能为空"),
            @Form(name = "payType", message = "请选择提现类型"),
            @Form(name = "payTo", message = "收款账号不能为空"),
    })
    public void doPayoutSubmit() {

        BigDecimal userAmount = userService.queryUserAmount(getLoginedUser().getId());
        if (userAmount == null || userAmount.compareTo(BigDecimal.ZERO) <= 0) {
            renderFailJson("余额不足，无法提现");
            return;
        }

        BigDecimal payoutAmount = new BigDecimal(getPara("payoutAmount"));
        if (userAmount.compareTo(payoutAmount) < 0) {
            renderFailJson("余额不足，无法提现");
            return;
        }

        UserAmountPayout payout = new UserAmountPayout();
        payout.setUserId(getLoginedUser().getId());
        payout.setUserRealName(getPara("realName"));
        payout.setUserIdcard(getPara("idcard"));
        payout.setAmount(payoutAmount);
        payout.setPayType(getPara("payType"));
        payout.setPayTo(getPara("payTo"));
        payout.setRemarks(getPara("remarks"));

        float feefloat = JPressOptions.getAsFloat("payout_fee", 0);
        if (feefloat < 0 || feefloat >= 1) {
            renderFailJson("网站管理员配置提现费率不正确，请联系管理员");
            return;
        }

        BigDecimal fee = payoutAmount.multiply(new BigDecimal(feefloat));
        payout.setFee(fee);
        payout.setStatus(UserAmountPayout.STATUS_APPLYING);

        payoutService.save(payout);
        renderOkJson();

    }


    /**
     * 金额充值页面
     */
    public void recharge() {
        PayConfigUtil.setConfigAttrs(this);
        render("recharge.html");
    }

    /**
     * 进行充值
     */
    @EmptyValidate({
            @Form(name = "paytype", message = "支付方式不能为空"),
            @Form(name = "recharge_amount", message = "充值金额不能空"),
    })
    public void recharging() {

        PaymentRecord payment = new PaymentRecord();
        payment.setProductTitle("账户充值");
        payment.setProductRelativeType("user_amount_statement");

        payment.setTrxNo(StrUtil.uuid());
        payment.setTrxType(PaymentRecord.TRX_TYPE_RECHARGE);
        payment.setTrxNonceStr(StrUtil.uuid());

        payment.setPayerUserId(getLoginedUser().getId());
        payment.setPayerName(getLoginedUser().getNickname());
        payment.setPayerFee(BigDecimal.ZERO);

        //预支付
        payment.setPayStatus(PayStatus.UNPAY.getStatus());

        payment.setOrderIp(getIPAddress());
        payment.setOrderRefererUrl(getReferer());

        payment.setPayAmount(new BigDecimal(getPara("recharge_amount")));
        payment.setPayType(getPara("paytype"));

        //预支付
        payment.setStatus(PaymentRecord.STATUS_PAY_PRE);


        PaymentRecordService paymentService = Aop.get(PaymentRecordService.class);
        paymentService.save(payment);

        renderJson(Ret.ok().set("gotoUrl", PayKit.buildPayUrl(payment.getPayType(), payment.getTrxNo())));
    }


}
