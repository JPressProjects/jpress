/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import io.jpress.JPressConsts;
import io.jpress.commons.email.SimplerEmailSender;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Option;
import io.jpress.service.OptionService;
import io.jpress.web.handler.JPressHandler;
import io.jpress.web.interceptor.ApiInterceptor;
import io.jpress.web.interceptor.TemplateInterceptor;
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
        JbootEventManager.me().registerListener(this, false, JPressConsts.EVENT_OPTION_UPDATE);

        initTemplateOption(); //初始化模板配置
        initFakeStaticOption(); //初始化伪静态配置
        initCdnOption(); //初始化CDN配置
        initApiOption(); //初始化 API 配置
        initWechatOption();// 初始化 微信的 相关配置

        initTemplateAttrsOption(); //初始化模板的基础 attr 属性
        initEmailOption();// 初始化邮件相关配置

    }

    private void initEmailOption() {
        Boolean emailEnable = service.findAsBoolByKey(JPressConsts.OPTION_CONNECTION_EMAIL_ENABLE);
        if (emailEnable == null || emailEnable == false) {
            SimplerEmailSender.setEnable(false);
            return;
        }

        SimplerEmailSender.setEnable(true);

        String smtp = service.findByKey(JPressConsts.OPTION_CONNECTION_EMAIL_SMTP);
        String account = service.findByKey(JPressConsts.OPTION_CONNECTION_EMAIL_ACCOUNT);
        String password = service.findByKey(JPressConsts.OPTION_CONNECTION_EMAIL_PASSWORD);
        Boolean sslEnable = service.findAsBoolByKey(JPressConsts.OPTION_CONNECTION_EMAIL_SSL_ENABLE);

        SimplerEmailSender.init(smtp, account, password, sslEnable == null ? false : sslEnable);

    }

    private void initTemplateAttrsOption() {

        String webTitle = service.findByKey(JPressConsts.OPTION_WEB_TITLE);
        String webSubTitle = service.findByKey(JPressConsts.OPTION_WEB_SUBTITLE);
        String webName = service.findByKey(JPressConsts.OPTION_WEB_NAME);
        String webDomain = service.findByKey(JPressConsts.OPTION_WEB_DOMAIN);
        String webCopyright = service.findByKey(JPressConsts.OPTION_WEB_COPYRIGHT);
        String seoTitle = service.findByKey(JPressConsts.OPTION_SEO_TITLE);
        String seoKeyword = service.findByKey(JPressConsts.OPTION_SEO_KEYWORDS);
        String seoDescription = service.findByKey(JPressConsts.OPTION_SEO_DESCRIPTION);


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

        String appId = service.findByKey(JPressConsts.OPTION_WECHAT_APPID);
        String appSecret = service.findByKey(JPressConsts.OPTION_WECHAT_APPSECRET);
        String token = service.findByKey(JPressConsts.OPTION_WECHAT_TOKEN);


        if (StrUtils.areNotEmpty(appId, appSecret, token)) {
            // 配置微信 API 相关参数
            ApiConfig ac = new ApiConfig();
            ac.setAppId(appId);
            ac.setAppSecret(appSecret);
            ac.setToken(token);
            ac.setEncryptMessage(false); //采用明文模式，同时也支持混合模式

            ApiConfigKit.putApiConfig(ac);
        }


        String miniProgramAppId = service.findByKey(JPressConsts.OPTION_WECHAT_MINIPROGRAM_APPID);
        String miniProgramAppSecret = service.findByKey(JPressConsts.OPTION_WECHAT_MINIPROGRAM_APPSECRET);
        String miniProgramToken = service.findByKey(JPressConsts.OPTION_WECHAT_MINIPROGRAM_TOKEN);

        if (StrUtils.areNotEmpty(miniProgramAppId, miniProgramAppSecret, miniProgramToken)) {
            WxaConfig wxaConfig = new WxaConfig();
            wxaConfig.setAppId(miniProgramAppId);
            wxaConfig.setAppSecret(miniProgramAppSecret);
            wxaConfig.setToken(miniProgramToken);
            wxaConfig.setMessageEncrypt(false); //采用明文模式，同时也支持混合模式

            WxaConfigKit.setWxaConfig(wxaConfig);
        }

    }


    private void initApiOption() {
        Boolean cdnEnable = service.findAsBoolByKey(JPressConsts.OPTION_API_ENABLE);
        if (cdnEnable == null || cdnEnable == false) {
            ApiInterceptor.initApiEnable(false);
        } else {
            ApiInterceptor.initApiEnable(true);
        }

        String apiAppid = service.findByKey(JPressConsts.OPTION_API_APPID);
        String apiSecret = service.findByKey(JPressConsts.OPTION_API_SECRET);
        ApiInterceptor.initApiSecret(apiAppid, apiSecret);
    }

    private void initCdnOption() {

        Boolean cdnEnable = service.findAsBoolByKey(JPressConsts.OPTION_CDN_ENABLE);
        if (cdnEnable == null || cdnEnable == false) {
            TemplateRender.initCdnDomain(null);
            return;
        }

        String cdnDomain = service.findByKey(JPressConsts.OPTION_CDN_DOMAIN);
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
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return;
        }

        String suffix = service.findByKey(JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX);
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
            case JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE:
            case JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX:
                initFakeStaticOption();
                break;
            case JPressConsts.OPTION_API_ENABLE:
            case JPressConsts.OPTION_API_SECRET:
                initApiOption();
                break;
            case JPressConsts.OPTION_CDN_DOMAIN:
            case JPressConsts.OPTION_CDN_ENABLE:
                initCdnOption();
                break;
            case JPressConsts.OPTION_WECHAT_APPID:
            case JPressConsts.OPTION_WECHAT_APPSECRET:
            case JPressConsts.OPTION_WECHAT_TOKEN:
                initWechatOption();
                break;
            case JPressConsts.OPTION_WEB_TITLE:
            case JPressConsts.OPTION_WEB_SUBTITLE:
            case JPressConsts.OPTION_WEB_NAME:
            case JPressConsts.OPTION_WEB_DOMAIN:
            case JPressConsts.OPTION_WEB_COPYRIGHT:
            case JPressConsts.OPTION_SEO_TITLE:
            case JPressConsts.OPTION_SEO_KEYWORDS:
            case JPressConsts.OPTION_SEO_DESCRIPTION:
                initTemplateAttrsOption();
                break;
            case JPressConsts.OPTION_CONNECTION_EMAIL_ENABLE:
            case JPressConsts.OPTION_CONNECTION_EMAIL_SMTP:
            case JPressConsts.OPTION_CONNECTION_EMAIL_ACCOUNT:
            case JPressConsts.OPTION_CONNECTION_EMAIL_PASSWORD:
            case JPressConsts.OPTION_CONNECTION_EMAIL_SSL_ENABLE:
                initEmailOption();
                break;
        }
    }


}
