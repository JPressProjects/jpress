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
package io.jpress.core.site;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.core.install.Installer;
import io.jpress.model.SiteInfo;
import io.jpress.service.SiteInfoService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SiteManager implements JbootEventListener {

    private static final Log LOG = Log.getLog(SiteManager.class);

    private static Set<String> ignoreDomains = new HashSet<>();
    static {
        ignoreDomains.add("127.0.0.1");
        ignoreDomains.add("localhost");
    }

    private static final SiteManager me = new SiteManager();

    public static SiteManager me() {
        return me;
    }

    private SiteInfoService siteInfoService;

    public void init() {
        if (Installer.notInstall()) {
            Installer.addListener(this);
        }else {
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
    private void doRealInit(){
        siteInfoService = Aop.get(SiteInfoService.class);
    }


    public SiteInfo matchedSiteId(String target, HttpServletRequest request){
        List<SiteInfo> allSites = siteInfoService.findAll();

        String reqDomain = request.getServerName();
        boolean isIgnoreDomain = ignoreDomains.contains(reqDomain);

        String siteId = CookieUtil.get(request, JPressConsts.COOKIE_SITE_ID);

        if (allSites != null  && !allSites.isEmpty()){
            for (SiteInfo site : allSites) {
                if (StrUtil.isNotBlank(site.getBindPath()) && target.startsWith(site.getBindPath())){
                    return site;
                }

                if (!isIgnoreDomain && reqDomain.equals(site.getBindDomain())){
                    return site;
                }

                if (siteId != null && siteId.equals(String.valueOf(site.getSiteId()))){
                    return site;
                }
            }
        }

        return null;
    }
}
