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
package io.jpress.web.seoping;


import io.jpress.JPressOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingManager {

    private static PingManager me = new PingManager();

    private PingManager() {
    }

    public static PingManager me() {
        return me;
    }


    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

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


}
