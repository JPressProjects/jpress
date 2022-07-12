package io.jpress.module.form.directive;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.form.bsformbuilder.FormManager;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

@JFinalDirective("formInfo")
public class FormInfoDirective extends JbootDirectiveBase {

    @Inject
    private FormInfoService formInfoService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        FormInfo formInfo = formInfoService.findById(getParaToLong("id", scope));
        JSONArray datas = JSONArray.parseArray(formInfo.getBuilderJson());

        String html = FormManager.me().renderAll(datas, false);
        renderText(writer, html);
    }
}
