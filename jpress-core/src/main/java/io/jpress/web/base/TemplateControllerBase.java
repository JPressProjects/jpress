package io.jpress.web.base;

import com.jfinal.aop.Before;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.web.render.TemplateRender;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({TemplateInterceptor.class, UserInterceptor.class})
public abstract class TemplateControllerBase extends ControllerBase {


    @Override
    public void render(String view) {

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            renderText("can not find current template");
            return;
        }

        view = template.matchTemplateFile(view);
        if (view == null) {
            renderText("can not match view to render");
            return;
        }

        view = template.getWebAbsolutePath() + "/" + view;
        super.render(new TemplateRender(view));
    }


    protected void assertNotNull(Object object) {
        if (object == null) {
            renderError(404);
        }
    }

}
