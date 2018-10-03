package io.jpress.web;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import io.jboot.Jboot;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventListener;
import io.jboot.event.JbootEventManager;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConstants;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Option;
import io.jpress.service.OptionService;
import io.jpress.web.base.ApiInterceptor;
import io.jpress.web.base.TemplateInterceptor;
import io.jpress.web.handler.JPressHandler;
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
        initApiOption(); //初始化 API 配置
        initWechatOption();// 初始化 微信的 相关配置

        initTemplateAttrsOption(); //初始化模板的基础 attr 属性

    }

    private void initTemplateAttrsOption() {

        String webTitle = service.findByKey(JPressConstants.OPTION_WEB_TITLE);
        String webSubTitle = service.findByKey(JPressConstants.OPTION_WEB_SUBTITLE);
        String webName = service.findByKey(JPressConstants.OPTION_WEB_NAME);
        String webDomain = service.findByKey(JPressConstants.OPTION_WEB_DOMAIN);
        String webCopyright = service.findByKey(JPressConstants.OPTION_WEB_COPYRIGHT);
        String seoTitle = service.findByKey(JPressConstants.OPTION_SEO_TITLE);
        String seoKeyword = service.findByKey(JPressConstants.OPTION_SEO_KEYWORDS);
        String seoDescription = service.findByKey(JPressConstants.OPTION_SEO_DESCRIPTION);


        TemplateInterceptor.setWebTitle(webTitle);
        TemplateInterceptor.setWebSubTitle(webSubTitle);
        TemplateInterceptor.setWebName(webName);
        TemplateInterceptor.setWebDomain(webDomain);
        TemplateInterceptor.setWebCopyright(webCopyright);
        TemplateInterceptor.setSeoTitle(seoTitle);
        TemplateInterceptor.setSeoKeyword(seoKeyword);
        TemplateInterceptor.setSeoDescription(seoDescription);

    }


    /**
     * 设置微信的相关配置
     */
    private void initWechatOption() {

        String appId = service.findByKey(JPressConstants.OPTION_WECHAT_APPID);
        String appSecret = service.findByKey(JPressConstants.OPTION_WECHAT_APPSECRET);
        String token = service.findByKey(JPressConstants.OPTION_WECHAT_TOKEN);


        if (StrUtils.isBlank(appId)
                || StrUtils.isBlank(appSecret)
                || StrUtils.isBlank(token)
                ) {
            // 配置微信 API 相关参数
            ApiConfig ac = new ApiConfig();
            ac.setAppId(appId);
            ac.setAppSecret(appSecret);
            ac.setToken(token);
            ac.setEncryptMessage(false); //采用明文模式，同时也支持混合模式

            ApiConfigKit.putApiConfig(ac);
        }


        String miniProgramAppId = service.findByKey(JPressConstants.OPTION_WECHAT_MINIPROGRAM_APPID);
        String miniProgramAppSecret = service.findByKey(JPressConstants.OPTION_WECHAT_MINIPROGRAM_APPSECRET);
        String miniProgramToken = service.findByKey(JPressConstants.OPTION_WECHAT_MINIPROGRAM_TOKEN);
        if (StrUtils.isBlank(appId)
                || StrUtils.isBlank(appSecret)
                || StrUtils.isBlank(token)
                ) {
            WxaConfig wxaConfig = new WxaConfig();
            wxaConfig.setAppId(miniProgramAppId);
            wxaConfig.setAppSecret(miniProgramAppSecret);
            wxaConfig.setToken(miniProgramToken);
            wxaConfig.setMessageEncrypt(false); //采用明文模式，同时也支持混合模式

            WxaConfigKit.setWxaConfig(wxaConfig);
        }

    }


    private void initApiOption() {
        Boolean cdnEnable = service.findAsBoolByKey(JPressConstants.OPTION_API_ENABLE);
        if (cdnEnable == null || cdnEnable == false) {
            ApiInterceptor.initApiEnable(false);
        } else {
            ApiInterceptor.initApiEnable(true);
        }

        String apiAppid = service.findByKey(JPressConstants.OPTION_API_APPID);
        String apiSecret = service.findByKey(JPressConstants.OPTION_API_SECRET);
        ApiInterceptor.initApiSecret(apiAppid, apiSecret);
    }

    private void initCdnOption() {

        Boolean cdnEnable = service.findAsBoolByKey(JPressConstants.OPTION_CDN_ENABLE);
        if (cdnEnable == null || cdnEnable == false) {
            TemplateRender.initCdnDomain(null);
            return;
        }

        String cdnDomain = service.findByKey(JPressConstants.OPTION_CDN_DOMAIN);
        if (StrUtils.isBlank(cdnDomain)) {
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
        if (StrUtils.isBlank(suffix)) {
            JPressHandler.initSuffix(null);
        } else {
            JPressHandler.initSuffix(suffix);
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
            case JPressConstants.OPTION_WECHAT_APPID:
            case JPressConstants.OPTION_WECHAT_APPSECRET:
            case JPressConstants.OPTION_WECHAT_TOKEN:
                initWechatOption();
                break;

            case JPressConstants.OPTION_WEB_TITLE:
            case JPressConstants.OPTION_WEB_SUBTITLE:
            case JPressConstants.OPTION_WEB_NAME:
            case JPressConstants.OPTION_WEB_DOMAIN:
            case JPressConstants.OPTION_WEB_COPYRIGHT:
            case JPressConstants.OPTION_SEO_TITLE:
            case JPressConstants.OPTION_SEO_KEYWORDS:
            case JPressConstants.OPTION_SEO_DESCRIPTION:
                initTemplateAttrsOption();
                break;
        }
    }


}
