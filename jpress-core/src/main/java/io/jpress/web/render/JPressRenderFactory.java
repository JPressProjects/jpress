package io.jpress.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.TextRender;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.render.JbootRenderFactory;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.render
 */
public class JPressRenderFactory extends JbootRenderFactory {

    @Override
    public Render getErrorRender(int errorCode) {

        /**
         * 如果是后台、api等其他非模板相关Controller，不用渲染。
         */
        if (!(JbootControllerContext.get() instanceof TemplateControllerBase)) {
            return super.getErrorRender(errorCode);
        }

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return new TextRender(errorCode + " error, bug can not find current template to render");
        }

        String view = template.matchTemplateFile("error_" + errorCode + ".html");
        if (view == null) {
            return super.getErrorRender(errorCode);
        }

        view = "/templates/" + template.getFolder() + "/" + view;
        return new TemplateRender(view);
    }


}
