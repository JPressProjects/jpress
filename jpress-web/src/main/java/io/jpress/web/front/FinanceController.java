package io.jpress.web.front;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.pay.PayConfigUtil;
import io.jpress.commons.pay.PayStatus;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;


@RequestMapping(value = "/ucenter/finance", viewPath = "/WEB-INF/views/ucenter/finance")
public class FinanceController extends UcenterControllerBase {


    @Inject
    private UserService userService;

    @Inject
    private UserAmountStatementService amountStatementService;



    /**
     * 用户余额信息
     */
    public void amount() {
        setAttr("userAmount",userService.queryUserAmount(getLoginedUser().getId()));
        setAttr("userAmountStatements",amountStatementService.findListByUserId(getLoginedUser().getId(),10));
        render("amount.html");
    }


    /**
     * 金额充值页面
     */
    @ActionKey("/ucenter/finance/amount/recharge")
    public void recharge() {
        PayConfigUtil.setConfigAttrs(this);
        render("recharge.html");
    }

    /**
     * 进行充值
     */
    @ActionKey("/ucenter/finance/amount/doRecharge")
    public void doRecharge() {

        PaymentRecord payment = new PaymentRecord();
        payment.setProductTitle("用户充值");
        payment.setProductType("recharge");
//        payment.setProductRelativeId();
//        payment.setProductDesc();

        payment.setTrxNo(StrUtil.uuid());
        payment.setTrxType(PaymentRecord.TRX_TYPE_RECHARGE);
        payment.setTrxNonceStr(StrUtil.uuid());

        payment.setPayerUserId(getLoginedUser().getId());
        payment.setPayerName(getLoginedUser().getNickname());
        payment.setPayerFee(BigDecimal.ZERO);
        payment.setPayStatus(PayStatus.UNPAY.getStatus());//预支付

        payment.setOrderIp(getIPAddress());
        payment.setOrderRefererUrl(getReferer());

        payment.setPayAmount(new BigDecimal(getPara("recharge_amount")));
        payment.setPayType(getPara("paytype"));


        payment.setStatus(PaymentRecord.STATUS_PAY_PRE); //预支付


        PaymentRecordService paymentService = Aop.get(PaymentRecordService.class);
        paymentService.save(payment);


        PayKit.redirect(payment.getPayType(), payment.getTrxNo());
    }


}
