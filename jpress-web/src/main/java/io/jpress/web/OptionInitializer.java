package io.jpress.web;

import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
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
import io.jpress.web.front.IndexController;
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
        initApiOption(); //初始化 API 配置
        initWechatOption();// 初始化 微信的 相关配置
        initWebIndexStyleOption();// 初始化 首页模板

    }

    private void initWebIndexStyleOption() {
        String indexStyle = service.findByKey(JPressConstants.OPTION_WEB_INDEX_STYLE);
        IndexController.initStyle(indexStyle);
    }

    /**
     * 设置微信的相关配置
     */
    private void initWechatOption() {

        String appId = service.findByKey(JPressConstants.OPTION_WECHAT_APPID);
        String appSecret = service.findByKey(JPressConstants.OPTION_WECHAT_APPSECRET);
        String token = service.findByKey(JPressConstants.OPTION_WECHAT_TOKEN);


        if (StringUtils.isBlank(appId)
                || StringUtils.isBlank(appSecret)
                || StringUtils.isBlank(token)
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
        if (StringUtils.isBlank(appId)
                || StringUtils.isBlank(appSecret)
                || StringUtils.isBlank(token)
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
            case JPressConstants.OPTION_WECHAT_APPID:
            case JPressConstants.OPTION_WECHAT_APPSECRET:
            case JPressConstants.OPTION_WECHAT_TOKEN:
                initWechatOption();
                break;
            case JPressConstants.OPTION_WEB_INDEX_STYLE:
                initWebIndexStyleOption();
                break;
        }
    }

}
