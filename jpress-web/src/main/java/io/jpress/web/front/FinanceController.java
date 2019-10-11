package io.jpress.web.front;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.core.ActionKey;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserOrder;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserOrderService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;


@RequestMapping(value = "/ucenter/finance", viewPath = "/WEB-INF/views/ucenter/finance")
public class FinanceController extends UcenterControllerBase {

    @Inject
    private UserOrderService orderService;


    /**
     * 用户订单列表
     */
    public void order() {
        render("order_list.html");
    }


    /**
     * 订单详情
     */
    @ActionKey("/ucenter/finance/order/detail")
    public void detail() {
        UserOrder order = orderService.findById(getIdPara());
        if (order == null || order.getBuyerId() == null || !order.getBuyerId().equals(getLoginedUser().getId())) {
            renderError(404);
            return;
        }

        setAttr("order", order);
        render("order_detail.html");
    }

    /**
     * 用户余额信息
     */
    public void amount() {
        render("amount.html");
    }


    /**
     * 金额充值页面
     */
    @ActionKey("/ucenter/finance/amount/recharge")
    public void recharge() {
        render("recharge.html");
    }

    /**
     * 进行充值
     */
    @ActionKey("/ucenter/finance/amount/doRecharge")
    public void doRecharge() {
        //充值流程
        //创建用户流水，状态为生效中...
        //创建用户payment

        PaymentRecord payment = new PaymentRecord();
        payment.setProductName("用户充值");
        payment.setProductType("recharge");

        payment.setTrxNo(StrUtil.uuid());
        payment.setTrxType("recharge");

        payment.setPayerUserId(getLoginedUser().getId());
        payment.setPayerName(getLoginedUser().getNickname());
        payment.setPayerFee(BigDecimal.ZERO);

        payment.setOrderIp(getIPAddress());
        payment.setOrderRefererUrl(getReferer());

        payment.setPayAmount(BigDecimal.valueOf(Long.valueOf(getPara("recharge_amount"))));
        payment.setStatus("1");

        PaymentRecordService paymentService = Aop.get(PaymentRecordService.class);
        paymentService.save(payment);


        redirect("/pay/" + getPara("paytype") + "/" + payment.getTrxNo());
    }


}
