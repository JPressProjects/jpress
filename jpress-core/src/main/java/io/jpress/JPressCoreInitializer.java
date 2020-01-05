/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.kit.PathKit;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.web.fixedinterceptor.FixedInterceptors;
import io.jpress.commons.utils.JPressJson;
import io.jpress.core.addon.AddonManager;
import io.jpress.core.addon.controller.AddonControllerProcesser;
import io.jpress.core.addon.handler.AddonHandlerProcesser;
import io.jpress.core.addon.interceptor.AddonInterceptorProcesser;
import io.jpress.core.install.InstallHandler;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.support.ehcache.EhcacheManager;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.web.captcha.JPressCaptchaCache;
import io.jpress.web.functions.JPressCoreFunctions;
import io.jpress.web.handler.JPressHandler;
import io.jpress.web.interceptor.JPressInterceptor;
import io.jpress.web.interceptor.UTMInterceptor;
import io.jpress.web.render.JPressRenderFactory;
import io.jpress.web.sitemap.SitemapHandler;
import io.jpress.web.sitemap.SitemapManager;

import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class JPressCoreInitializer extends JbootAppListenerBase {

    @Override
    public void onInit() {
        try {
            URL resourceUrl = JPressCoreInitializer.class.getResource("/");
            if (resourceUrl != null) {
                PathKit.setWebRootPath(resourceUrl.toURI().getPath());
            }
            EhcacheManager.init();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConstantConfig(Constants constants) {

        constants.setRenderFactory(new JPressRenderFactory());
        constants.setCaptchaCache(new JPressCaptchaCache());
        constants.setJsonFactory(() -> new JPressJson());

    }

    @Override
    public void onRouteConfig(Routes routes) {
        routes.setClearAfterMapping(false);
    }

    @Override
    public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
        fixedInterceptors.add(new AddonInterceptorProcesser());
    }


    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {
        handlers.add(new InstallHandler());
        handlers.add(new SitemapHandler());
        handlers.add(new JPressHandler());
        handlers.add(new AddonHandlerProcesser());

        handlers.setActionHandler(new AddonControllerProcesser());
    }

    @Override
    public void onEngineConfig(Engine engine) {
        engine.addSharedStaticMethod(JPressCoreFunctions.class);
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        interceptors.add(new UTMInterceptor());
        interceptors.add(new JPressInterceptor());
    }

    @Override
    public void onStart() {

        SitemapManager.me().init();
        MenuManager.me().init();
        WechatAddonManager.me().init();
        AddonManager.me().init();

    }

}
