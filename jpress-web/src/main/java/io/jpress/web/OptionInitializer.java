/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Aop;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.core.install.Installer;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Option;
import io.jpress.service.OptionService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于在应用启动的时候，读取数据库的配置信息进行某些配置
 * @Package io.jpress.web
 */
public class OptionInitializer implements JPressOptions.OptionChangeListener, JbootEventListener {

    private static OptionInitializer me = new OptionInitializer();

    private OptionInitializer() {

    }

    public static OptionInitializer me() {
        return me;
    }

    public void init() {

        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }

        OptionService service = Aop.get(OptionService.class);

        List<Option> options = service.findAll();
        for (Option option : options) {
            //整个网站的后台配置不超过100个，再未来最多也100多个，所以全部放在内存毫无压力
            if (StrUtil.isNotBlank(option.getValue()) && option.getSiteId() != null) {
                JPressOptions.set(option.getKey(), option.getValue(), option.getSiteId());
            }
        }

        //初始化模板配置
        TemplateManager.me().init();

        // 初始化 微信公众号 的配置
        initWechatOption();

        // 初始化 微信小程序 的配置
        initWechatMiniProgramOption();


        JPressOptions.addListener(this);

    }


    /**
     * 设置微信的相关配置
     */
    private void initWechatOption() {

        String appId = JPressOptions.get(JPressConsts.OPTION_WECHAT_APPID);
        String appSecret = JPressOptions.get(JPressConsts.OPTION_WECHAT_APPSECRET);
        String token = JPressOptions.get(JPressConsts.OPTION_WECHAT_TOKEN);

        if (StrUtil.areNotEmpty(appId, appSecret, token)) {
            // 配置微信 API 相关参数
            ApiConfig ac = new ApiConfig();
            ac.setAppId(appId);
            ac.setAppSecret(appSecret);
            ac.setToken(token);
            ac.setEncryptMessage(false); //采用明文模式，同时也支持混合模式

            //重新设置后，需要清空之前的配置。
            ApiConfigKit.removeAll();

            ApiConfigKit.putApiConfig(ac);
        }

    }

    private void initWechatMiniProgramOption() {

        String miniProgramAppId = JPressOptions.get(JPressConsts.OPTION_WECHAT_MINIPROGRAM_APPID);
        String miniProgramAppSecret = JPressOptions.get(JPressConsts.OPTION_WECHAT_MINIPROGRAM_APPSECRET);
//        String miniProgramToken = JPressOptions.get(JPressConsts.OPTION_WECHAT_MINIPROGRAM_TOKEN);

        if (StrUtil.areNotEmpty(miniProgramAppId, miniProgramAppSecret)) {
            WxaConfig wxaConfig = new WxaConfig();
            wxaConfig.setAppId(miniProgramAppId);
            wxaConfig.setAppSecret(miniProgramAppSecret);
//            wxaConfig.setToken(miniProgramToken);
            wxaConfig.setMessageEncrypt(false); //采用明文模式，同时也支持混合模式

            WxaConfigKit.setWxaConfig(wxaConfig);
        }

    }


    @Override
    public void onChanged(Long siteId, String key, String newValue, String oldValue) {
        switch (key) {
            case JPressConsts.OPTION_WECHAT_APPID:
            case JPressConsts.OPTION_WECHAT_APPSECRET:
            case JPressConsts.OPTION_WECHAT_TOKEN:
                initWechatOption();
                break;
            case JPressConsts.OPTION_WECHAT_MINIPROGRAM_APPID:
            case JPressConsts.OPTION_WECHAT_MINIPROGRAM_APPSECRET:
            case JPressConsts.OPTION_WECHAT_MINIPROGRAM_TOKEN:
                initWechatMiniProgramOption();
                break;
        }
    }

    @Override
    public void onEvent(JbootEvent jbootEvent) {
        init();
    }
}
