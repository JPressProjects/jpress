package io.jpress.module.form.controller.front;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.TemplateControllerBase;

@RequestMapping("/form")
public class FormController extends TemplateControllerBase {

    private static final String DEFAULT_FORM_DETAIL_TEMPLATE = "/WEB-INF/views/front/form/detail.html";


    @Inject
    private FormInfoService formInfoService;


    public void index(){
        Long formId = getParaToLong();
        if (formId == null){
            renderError(404);
            return;
        }

        FormInfo formInfo = formInfoService.findById(formId);
        setAttr("formInfo",formInfo);

        render("form.html",DEFAULT_FORM_DETAIL_TEMPLATE);
    }




    /**
     * 提交数据到 form
     */
    public void postData(){

    }
}
