package io.jpress.module.form.directive;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.form.FormManager;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

import java.util.Enumeration;
import java.util.Map;

@JFinalDirective("formInfo")
public class FormInfoDirective extends JbootDirectiveBase {

    @Inject
    private FormInfoService formInfoService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Long id = getParaToLong("id", scope);
        String flag = getParaToString("flag", scope);
        boolean withForm = getParaToBool("withForm", scope, true);

        String formClass = getParaToString("formClass", scope, "formInfo");
        String formMethod = getParaToString("formMethod", scope, "POST");

        String submitText = getParaToString("submitText", scope, "提交");
        String submitClass = getParaToString("submitClass", scope, "btn btn-primary btn-form-submit");

        Map values = getPara("values", scope);


        FormInfo formInfo = null;
        if (id != null || flag != null) {
            formInfo = id != null
                    ? formInfoService.findById(id)
                    : formInfoService.findFirstByColumns(Columns.create("flag", flag));
        } else {
            formInfo = getFormInfoInAttrs();
        }

        if (formInfo == null) {
            throw new IllegalArgumentException("#formInfo() id or flag must not be null.");
        }


        JSONArray datas = JSONArray.parseArray(formInfo.getBuilderJson());
        if (withForm) {
            String action = formInfo.getActionUrl();
            String htmlStart = "<form id=\"form" + formInfo.getId() + "\" class=\"" + formClass + "\" method=\"" + formMethod + "\" action=\"" + action + "\">";
            String htmlEnd = "<button class=\"" + submitClass + "\" type=\"submit\">" + submitText + "</button>" +
                    "</form>";

            String html = FormManager.me().renderAll(datas, values, false);
            renderText(writer, htmlStart + html + htmlEnd);
        } else {
            String html = FormManager.me().renderAll(datas, values, false);
            renderText(writer, html);
        }
    }


    private FormInfo getFormInfoInAttrs() {
        Controller controller = JbootControllerContext.get();
        if (controller == null) {
            return null;
        }

        Enumeration<String> attrs = controller.getAttrNames();
        while (attrs.hasMoreElements()) {
            String attr = attrs.nextElement();
            Object value = controller.getAttr(attr);
            if (value instanceof FormInfo) {
                return (FormInfo) value;
            }
        }

        return null;
    }
}
