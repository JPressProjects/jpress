package io.jpress.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.TextRender;
import io.jboot.web.render.JbootRenderFactory;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.render
 */
public class JPressRenderFactory extends JbootRenderFactory {


    @Override
    public Render getErrorRender(int errorCode) {
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return new TextRender("can not find current template");
        }

        String view = template.matchTemplateFile("error_" + errorCode + ".html");
        if (view == null) {
            return super.getErrorRender(errorCode);
        }

        view = "/templates/" + template.getFolder() + "/" + view;
        return new TemplateRender(view);
    }


}
