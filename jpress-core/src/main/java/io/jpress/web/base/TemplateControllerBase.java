package io.jpress.web.base;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.JbootController;
import io.jpress.JPressAppConfig;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.service.OptionService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
public abstract class TemplateControllerBase extends JbootController {

    @Inject
    private OptionService optionService;

    @Override
    public void render(String view) {
        String templateId = optionService.findByKey("web_template");
        templateId = StringUtils.isNotBlank(templateId) ? templateId : JPressAppConfig.me.getDefaultTemplate();

        Template template = TemplateManager.me().getTemplateById(templateId);
        if (template == null) {
            renderText("template : " + templateId + " is not install");
            return;
        }

        view = "/templates/" + template.getFolder() + "/" + view;
        super.render(view);
    }
}
