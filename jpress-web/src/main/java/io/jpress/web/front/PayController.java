package io.jpress.web.front;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.web.base.TemplateControllerBase;

@RequestMapping(value = "/pay",viewPath = "/WEB-INF/views/front/pay")
public class PayController extends TemplateControllerBase {

    @Inject
    private PaymentRecordService paymentService;



    public void index(){
        String trxNo = getPara();
        PaymentRecord payment = paymentService.findByTrxNo(trxNo);
        render404If(payment == null);

        String payType = getPara("type");
        initPayInfo(payType);

        render(payType+".html");
    }

    private boolean initPayInfo(String payType) {
        switch (payType){
            case "wechat":
                return initWechatInfo();
            case "wechatx":
                return initWechatxInfo();
            case "alipay":
                return initAlipayInfo();
            case "alipayx":
                return initAlipayxInfo();
            default:
                return false;
        }
    }



    private boolean initWechatInfo() {
        return false;
    }

    private boolean initWechatxInfo() {
        return false;
    }

    private boolean initAlipayInfo() {
        return false;
    }

    private boolean initAlipayxInfo() {
        return false;
    }

    public void query(){

    }

    public void exec(){
        renderText("exec");
    }

    public void callback(){
        renderText("callback");
    }

}
