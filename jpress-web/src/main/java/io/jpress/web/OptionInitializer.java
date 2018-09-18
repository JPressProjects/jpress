package io.jpress.web;

import io.jboot.Jboot;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventListener;
import io.jboot.event.JbootEventManager;
import io.jboot.utils.StringUtils;
import io.jpress.JPressConstants;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Option;
import io.jpress.service.OptionService;
import io.jpress.web.base.ApiInterceptor;
import io.jpress.web.handler.FakeStaticHandler;
import io.jpress.web.render.TemplateRender;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于在应用启动的时候，读取数据库的配置信息进行某些配置
 * @Package io.jpress.web
 */
public class OptionInitializer implements JbootEventListener {

    private static OptionInitializer me = new OptionInitializer();

    private OptionInitializer() {

    }

    private OptionService service;

    public static OptionInitializer me() {
        return me;
    }

    public void init() {

        service = Jboot.bean(OptionService.class);
        JbootEventManager.me().registerListener(this, false, JPressConstants.EVENT_OPTION_UPDATE);

        initTemplateOption(); //初始化模板配置
        initFakeStaticOption(); //初始化伪静态配置
        initCdnOption(); //初始化CDN配置
        initApiOption();

    }

    private void initApiOption() {
        Boolean cdnEnable = service.findAsBoolByKey(JPressConstants.OPTION_API_ENABLE);
        if (cdnEnable == null || cdnEnable == false) {
            ApiInterceptor.initApiEnable(false);
        } else {
            ApiInterceptor.initApiEnable(true);
        }

        String apiSecret = service.findByKey(JPressConstants.OPTION_API_SECRET);
        ApiInterceptor.initApiSecret(apiSecret);
    }

    private void initCdnOption() {

        Boolean cdnEnable = service.findAsBoolByKey(JPressConstants.OPTION_CDN_ENABLE);
        if (cdnEnable == null || cdnEnable == false) {
            return;
        }

        String cdnDomain = service.findByKey(JPressConstants.OPTION_CDN_DOMAIN);
        if (StringUtils.isBlank(cdnDomain)) {
            TemplateRender.initCdnDomain(null);
        } else {
            TemplateRender.initCdnDomain(cdnDomain);
        }
    }


    /**
     * 初始化模板配置
     */
    private void initTemplateOption() {
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


    @Override
    public void onEvent(JbootEvent event) {
        Option option = event.getData();
        switch (option.getKey()) {
            case JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE:
            case JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX:
                initFakeStaticOption();
                break;
            case JPressConstants.OPTION_API_ENABLE:
            case JPressConstants.OPTION_API_SECRET:
                initApiOption();
                break;
            case JPressConstants.OPTION_CDN_DOMAIN:
            case JPressConstants.OPTION_CDN_ENABLE:
                initCdnOption();
                break;
//            case JPressConstants.option:
//            case JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX:
//                initFakeStaticOption();
//                break;
        }

//        /**
//         * 伪静态开关的设置
//         */
//        if (JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE.equals(option.getKey())) {
//
//            Boolean enable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
//            if (enable == null || enable == false) {
//                FakeStaticHandler.initSuffix(null);
//                return;
//            }
//
//            String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
//            if (StringUtils.isBlank(suffix)) {
//                FakeStaticHandler.initSuffix(null);
//            } else {
//                FakeStaticHandler.initSuffix(suffix);
//            }
//
//        }
//
//        /**
//         * 伪静态后缀的设置
//         */
//        else if (JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX.equals(option.getKey())) {
//            Boolean enable = service.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
//            if (enable == null || enable == false) {
//                return;
//            }
//
//            String suffix = service.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
//            if (StringUtils.isBlank(suffix)) {
//                FakeStaticHandler.initSuffix(null);
//            } else {
//                FakeStaticHandler.initSuffix(suffix);
//            }
//        }

    }
}
