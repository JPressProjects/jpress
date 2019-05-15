package io.jpress.web.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.Dict;
import io.jpress.service.DictService;

import java.io.IOException;
import java.util.List;

/**
 * 字典标签
 *
 * @author Eric.Huang
 * @date 2019-04-29 17:55
 * @package io.jpress.web.directive
 **/

@JFinalDirective("dict")
public class DictDirective extends JbootDirectiveBase {

    @Inject
    private DictService dictService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String dictType = getPara(0, scope);
        String boxType = getPara(1, scope, "select");
        String dictValue = getPara(2, scope);
        String propName = getPara(3, scope);
        String clickEvent = getPara(4, scope);

        if (StrUtil.isBlank(dictType)) {
            throw new IllegalArgumentException("paramter of dict type must not be null");
        }

        List<Dict> list = dictService.findByType(dictType);
        StringBuilder htmlBuilder = new StringBuilder();

        for(Dict dict : list) {

            if ("select".equals(boxType)) {

                if (StrUtil.isNotEmpty(dictValue) && dictValue.equals(dict.getValue())) {
                    htmlBuilder.append("<option value = \"" + dict.getValue() + "\" selected = \"selected\" >");
                } else {
                    htmlBuilder.append("<option value = \"" + dict.getValue() + "\" >");
                }

                htmlBuilder.append(dict.getName());
                htmlBuilder.append("</option>");

            } else if ("checkbox".equals(boxType)) {

                boolean isChecked = false;
                String[] dv = dictValue.split(",");
                int len = dv.length;
                for (int i = 0; i < len; i++) {
                    if (StrUtil.isNotBlank(dv[i]) && dv[i].equals(dict.getValue())) {
                        isChecked = true;
                    }
                }

                htmlBuilder.append("<label class=\"checkbox-inline\">");

                if (StrUtil.isNotBlank(clickEvent)) {
                    if (isChecked) {
                        htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" checked onclick=\"" + clickEvent + "('" + propName + "')\" >");
                    } else {
                        htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" onclick=\"" + clickEvent + "('" + propName + "')\" >");
                    }
                } else {
                    if (isChecked) {
                        htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" checked >");
                    } else {
                        htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" >");
                    }
                }

                htmlBuilder.append(dict.getName());
                htmlBuilder.append("</label>");
            }
        }

        try {
            writer.write(htmlBuilder.toString());
        } catch (IOException e) {
            throw new TemplateException(e.getMessage(), location, e);
        }
    }
}
