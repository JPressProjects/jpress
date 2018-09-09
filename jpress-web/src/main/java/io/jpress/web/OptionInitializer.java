package io.jpress.web;

import io.jboot.Jboot;
import io.jpress.core.template.TemplateManager;
import io.jpress.service.OptionService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于在应用启动的时候，读取数据库的配置信息进行某些配置
 * @Package io.jpress.web
 */
public class OptionInitializer {

    private static OptionInitializer me = new OptionInitializer();

    private OptionInitializer() {
    }

    private OptionService service;

    public static OptionInitializer me() {
        return me;
    }

    public void init() {
        service = Jboot.bean(OptionService.class);

        initTemplate();
    }

    /**
     * 初始化模板配置
     */
    private void initTemplate() {
        String templateId = service.findByKey("web_template");
        TemplateManager.me().setCurrentTemplate(templateId);
    }
}
