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


        render(getPara("type")+".html");
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
