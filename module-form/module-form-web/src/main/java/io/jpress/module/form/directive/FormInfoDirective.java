package io.jpress.module.form.directive;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.core.JFinal;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.SiteContext;
import io.jpress.module.form.bsformbuilder.FormManager;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

@JFinalDirective("formInfo")
public class FormInfoDirective extends JbootDirectiveBase {

    @Inject
    private FormInfoService formInfoService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Long id = getParaToLong("id", scope);
        String flag = getParaToString("flag", scope);

        String formClass = getParaToString("formClass", scope, "formInfo");
        String formMethod = getParaToString("formMethod", scope, "POST");

        String submitText = getParaToString("submitText", scope, "提交");
        String submitClass = getParaToString("submitClass", scope, "btn btn-primary btn-formInfo");


        if (id == null && flag == null) {
            throw new IllegalArgumentException("id or flag must not be null.");
        }


        FormInfo formInfo = id != null
                ? formInfoService.findById(id)
                : formInfoService.findFirstByColumns(Columns.create("flag", flag));

        JSONArray datas = JSONArray.parseArray(formInfo.getBuilderJson());

        String action = JFinal.me().getContextPath() + SiteContext.getSitePath() + "/form/postData/" + formInfo.getId();
        String htmlStart = "<form id=\"form-" + formInfo.getId() + "\" class=\"" + formClass + "\" method=\"" + formMethod + "\" action=\"" + action + "\">";
        String htmlEnd = "<button class=\"" + submitClass + "\" type=\"submit\">" + submitText + "</button>" +
                "</form>";

        String html = FormManager.me().renderAll(datas, false);
        renderText(writer, htmlStart + html + htmlEnd);
    }
}
