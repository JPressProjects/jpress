package io.jpress.module.form.controller.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormDataService;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.TemplateControllerBase;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/form")
public class FormController extends TemplateControllerBase {

    private static final String DEFAULT_FORM_DETAIL_TEMPLATE = "/WEB-INF/views/front/form/detail.html";
    private static final String DEFAULT_FORM_DETAIL_ARTICLE = "/WEB-INF/views/front/form/form_article_detail.html";


    @Inject
    private FormInfoService formInfoService;

    @Inject
    private FormDataService formDataService;


    public void index() {
        Long formId = getParaToLong();
        if (formId == null) {
            renderError(404);
            return;
        }

        FormInfo formInfo = formInfoService.findById(formId);
        if (formInfo == null) {
            renderError(404);
            return;
        }


        setAttr("form", formInfo);

        render("form.html", DEFAULT_FORM_DETAIL_TEMPLATE);
    }


    /**
     * 提交数据到 form
     */
    public void postData() {
        Long formId = getParaToLong();
        if (formId == null) {
            renderError(404);
            return;
        }

        FormInfo formInfo = formInfoService.findById(formId);
        if (formInfo == null || !formInfo.isPublished()) {
            renderError(404);
            return;
        }

        try {
            // parseRequestToRecord 可能会出现数据转换异常，需要告知前端
            Record record = formInfo.parseRequestToRecord(getRequest());
            formDataService.save(formInfo.getCurrentTableName(), record);
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Ret.fail().set("message", e.getMessage()));
            return;
        }


        renderOkJson();
    }


    /**
     * 获取表单数据
     */
    public void detail() {

        Long formId = getParaToLong();

        FormInfo formInfo = formInfoService.findById(formId);

        if (formId == null || formInfo == null) {
            renderError(404);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("formId", formId);


        String resultHtml = renderToString(DEFAULT_FORM_DETAIL_ARTICLE, map);

        map.put("state", true);
        map.put("html", resultHtml);

        renderJson(map);

    }


}
