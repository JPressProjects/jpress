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
package io.jpress;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.components.cache.JbootCache;
import io.jboot.components.cache.JbootCacheManager;
import io.jboot.components.cache.support.JbootCaptchaCache;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.core.weight.Weight;
import io.jboot.web.xss.XSSHandler;
import io.jpress.commons.url.FlatUrlHandler;
import io.jpress.core.addon.AddonManager;
import io.jpress.core.addon.controller.AddonControllerProcesser;
import io.jpress.core.addon.handler.AddonHandlerProcesser;
import io.jpress.core.install.InstallHandler;
import io.jpress.core.install.Installer;
import io.jpress.core.menu.MenuManager;
import io.jpress.core.site.SiteManager;
import io.jpress.core.template.BlockFunctions;
import io.jpress.core.wechat.WechatAddonManager;
import io.jpress.web.functions.JPressCoreFunctions;
import io.jpress.web.handler.JPressHandler;
import io.jpress.web.handler.SiteHandler;
import io.jpress.web.interceptor.JPressInterceptor;
import io.jpress.web.interceptor.UTMInterceptor;
import io.jpress.web.render.JPressRenderFactory;
import io.jpress.web.sitemap.SitemapHandler;
import io.jpress.web.sitemap.SitemapManager;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
@Weight(100)
public class JPressCoreInitializer extends JbootAppListenerBase {


    @Override
    public void onConstantConfig(Constants constants) {
        constants.setRenderFactory(new JPressRenderFactory());
        constants.setCaptchaCache(new JbootCaptchaCache());

        JbootCache cache = JbootCacheManager.me().getCache();
        cache.addThreadCacheNamePrefixIngores("user_sessions", "user_permission", "user_role", "useropenid", "site_info"
               ,"_aj_captcha" , "jboot_captchas", "jboot_tokens", "wechat_access_tokens");

    }

    @Override
    public void onRouteConfig(Routes routes) {
        routes.setClearAfterMapping(false);
    }


    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {

        if (!Installer.isInstalled()) {
            handlers.add(new InstallHandler());
        }

        handlers.add(new SitemapHandler());
        handlers.add(new JPressHandler());
        handlers.add(new SiteHandler());
        handlers.add(new FlatUrlHandler());
        handlers.add(new AddonHandlerProcesser());
        handlers.add(new XSSHandler());

        handlers.setActionHandler(new AddonControllerProcesser());
    }

    @Override
    public void onEngineConfig(Engine engine) {
        engine.addSharedStaticMethod(JPressCoreFunctions.class);
        engine.addSharedStaticMethod(BlockFunctions.class);
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
        SiteManager.me().init();

    }

}
