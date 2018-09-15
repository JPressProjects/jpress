package io.jpress.web;

import io.jboot.Jboot;
import io.jboot.utils.StringUtils;
import io.jpress.JPressConstants;
import io.jpress.core.template.TemplateManager;
import io.jpress.service.OptionService;
import io.jpress.web.handler.FakeStaticHandler;

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

        initFakeStaticOption();
    }


    /**
     * 初始化模板配置
     */
    private void initTemplate() {
        String templateId = service.findByKey("web_template");
        TemplateManager.me().setCurrentTemplate(templateId);
    }


    /**
     * 初始化 伪静态
     */
    private void initFakeStaticOption() {
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return;
        }

        String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
        if (StringUtils.isBlank(suffix)) {
            FakeStaticHandler.initSuffix(null);
        } else {
            FakeStaticHandler.initSuffix(suffix);
        }

    }
}
