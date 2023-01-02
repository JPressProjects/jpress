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
package io.jpress.web.seoping;


import com.jfinal.kit.LogKit;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import java.util.concurrent.*;


public class SeoManager {

    private static SeoManager me = new SeoManager();

    private SeoManager() {
    }

    public static SeoManager me() {
        return me;
    }


    private ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3,"seo-ping");

    private BaiduPinger baiduPinger = new BaiduPinger();
    private GooglePinger googlePinger = new GooglePinger();

    public void ping(PingData data) {

        boolean baiduPingEnable = JPressOptions.getAsBool("seo_baidu_ping_enable");
        if (baiduPingEnable) {
            fixedThreadPool.execute(() -> {
                baiduPinger.ping(data);
            });
        }

        boolean googlePingEnable = JPressOptions.getAsBool("seo_google_ping_enable");
        if (googlePingEnable) {
            fixedThreadPool.execute(() -> {
                googlePinger.ping(data);
            });
        }
    }


    public void baiduPush(String... urls) {
        doPushOrUpdate(true, urls);
    }


    public void baiduUpdate(String... urls) {
        doPushOrUpdate(false, urls);
    }


    private void doPushOrUpdate(boolean pushAction, String... urls) {
        if (urls == null || urls.length == 0) {
            return;
        }

        boolean baiduRealTimePushEnable = JPressOptions.getAsBool("seo_baidu_realtime_push_enable");
        if (!baiduRealTimePushEnable) {
            return;
        }

        String site = JPressOptions.get("seo_baidu_realtime_push_site");
        String token = JPressOptions.get("seo_baidu_realtime_push_token");
        if (!StrUtil.areNotEmpty(site, token)) {
            LogKit.error("site or token is empty , can not update to baidu");
            return;
        }

        String[] newUrls = appendDomainIfNecessary(urls);
        if (pushAction) {
            fixedThreadPool.execute(() -> BaiduSeoProcesser.push(site, token, newUrls));
        } else {
            fixedThreadPool.execute(() -> BaiduSeoProcesser.update(site, token, newUrls));
        }
    }


    private String[] appendDomainIfNecessary(String[] urls) {
        String[] newUrls = new String[urls.length];
        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];
            if (!url.startsWith("http")) {
                String domain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN, RequestUtil.getBaseUrl());
                newUrls[i] = domain + url;
            }
        }

        return newUrls;
    }


}
