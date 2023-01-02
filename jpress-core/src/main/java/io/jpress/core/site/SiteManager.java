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
package io.jpress.core.site;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.core.install.Installer;
import io.jpress.model.SiteInfo;
import io.jpress.service.SiteInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


public class SiteManager implements JbootEventListener {

    private static final Log LOG = Log.getLog(SiteManager.class);

    private static Set<String> ignoreDomains = new HashSet<>();

    static {
        ignoreDomains.add("127.0.0.1");
        ignoreDomains.add("localhost");
        ignoreDomains.add("0.0.0.0");
    }

    private static final SiteManager me = new SiteManager();

    public static SiteManager me() {
        return me;
    }

    private SiteInfoService siteInfoService;

    public void init() {
        if (Installer.notInstall()) {
            Installer.addListener(this);
        } else {
            doRealInit();
        }
    }


    @Override
    public void onEvent(JbootEvent event) {
        init();
    }


    /**
     * 真正的初始化
     */
    private void doRealInit() {
        siteInfoService = Aop.get(SiteInfoService.class);
    }


    public SiteInfo matchSite(String target, HttpServletRequest request, HttpServletResponse response) {
        if (siteInfoService == null) {
            return null;
        }

        if (target.startsWith("/commons/")){
            return null;
        }

        List<SiteInfo> allSites = siteInfoService.findAll();
        if (allSites == null || allSites.isEmpty()) {
            return null;
        }

        //后台
        if (target.startsWith("/admin")) {
            String loginedUserId = CookieUtil.get(request, JPressConsts.COOKIE_UID);
            if (StrUtil.isBlank(loginedUserId)) {
                return null;
            }
            String siteId = CookieUtil.get(request, JPressConsts.COOKIE_ADMIN_SITE_ID);
            for (SiteInfo site : allSites) {
                if (siteId != null && siteId.equals(String.valueOf(site.getSiteId()))) {
                    return site;
                }
            }
        }
        //前台
        else {
            // 首先做域名匹配
            // 在做绑定二级目录匹配
            // 最后做语言匹配
            String requestDomain = request.getServerName();
            boolean isIgnoreDomain = ignoreDomains.contains(requestDomain);

            List<SiteInfo> matchedSites = new ArrayList<>();

            //开始 域名匹配
            if (!isIgnoreDomain) {
                for (SiteInfo site : allSites) {
                    if (StrUtil.isNotBlank(site.getBindDomain()) && requestDomain.equals(site.getBindDomain())) {
                        matchedSites.add(site);
                    }
                }
            }

            //若域名匹配不到，则开始 二级目录匹配
            if (matchedSites.isEmpty()) {
                for (SiteInfo site : allSites) {
                    if (StrUtil.isNotBlank(site.getBindPath()) && target.startsWith(site.getBindPath())) {
                        return site;
                    }
                }
            }
            //若域名匹配到了，则再次对已经匹配到的进行目录匹配
            else {
                for (SiteInfo matchedSite : matchedSites) {
                    if (StrUtil.isNotBlank(matchedSite.getBindPath()) && target.startsWith(matchedSite.getBindPath())) {
                        return matchedSite;
                    }
                }

                //若在匹配的域名下，无法对二级目录匹配，空目录配置的站点
                for (SiteInfo matchedSite : matchedSites) {
                    if (StrUtil.isBlank(matchedSite.getBindPath())) {
                        return matchedSite;
                    }
                }
            }

            String redirectByBrowserLang = JPressOptions.getBySiteId("site_redirect_by_browser_lang", 0L);
            if (!"true".equalsIgnoreCase(redirectByBrowserLang)) {
                return null;
            }

            String lang = request.getParameter("lang");
            if ("1".equals(lang)) {
                CookieUtil.remove(response, JPressConsts.COOKIE_SITE_LANG_CLOSE);
            } else {
                String closeLangMatch = CookieUtil.get(request, JPressConsts.COOKIE_SITE_LANG_CLOSE);
                if ("1".equals(closeLangMatch)) {
                    return null;
                }
            }

            //关闭语言匹配
            if ("0".equals(lang)) {
                //设置 maxAgeInSeconds 为 -1，则意味着关闭浏览器后  cookies 失效
                CookieUtil.put(response, JPressConsts.COOKIE_SITE_LANG_CLOSE, "1", -1);
                return null;
            }


            Enumeration<Locale> locales = request.getLocales();

            //if 只匹配第一个，原因是：一般情况下，默认的浏览器都会自带好几个语言，比如中国的浏览器默认第一个是中文，第二个是英语，第三个是意大利..
            if (locales.hasMoreElements()) {
                Locale locale = locales.nextElement();
//                String localeString = locale.toString();
//                String language = locale.getLanguage();
//                String country = locale.getCountry();
                for (SiteInfo site : allSites) {
                    Set<String> bindLangs = site.getBindLangsAsSet();
                    if (bindLangs != null && bindLangs.contains(locale.toString())) {
                        return site;
                    }
                }
            }
        }


        return null;
    }
}
