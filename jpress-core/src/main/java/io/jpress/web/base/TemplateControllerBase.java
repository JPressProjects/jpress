package io.jpress.web.base;

import io.jboot.web.controller.JbootController;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.web.render.TemplateRender;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
public abstract class TemplateControllerBase extends JbootController {


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

        view = "/templates/" + template.getFolder() + "/" + view;
        super.render(new TemplateRender(view));
    }

}
