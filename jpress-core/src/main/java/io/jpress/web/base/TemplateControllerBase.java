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


    public void render(String view) {
        render(view, null);
    }

    public void render(String view, String defaultView) {

        //如果是 / 开头的文件，就不通过模板文件去渲染。而是去根目录去查找。
        if (view != null && view.startsWith("/")) {
            super.render(view);
            return;
        }

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            renderDefault(defaultView);
            return;
        }

        view = template.matchTemplateFile(view);
        if (view == null) {
            renderDefault(defaultView);
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

    private void renderDefault(String defaultView) {
        if (defaultView == null) {
            renderText("can not match view to render");
            return;
        } else {
            super.render(new TemplateRender(defaultView));
        }
    }


}
