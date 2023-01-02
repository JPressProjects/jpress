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


import com.jfinal.log.Log;
import io.jboot.utils.HttpUtil;

public class BaiduPinger implements Pinger {

    private static final Log log = Log.getLog(BaiduPinger.class);

    private static final String xml = "" +
            "<?xml version=\"1.0″ encoding=\"UTF-8″?> \n" +
            "<methodCall> \n" +
            "<methodName>weblogUpdates.ping</methodName> \n" +
            "<params> \n" +
            "{template}" +
            "</params> \n" +
            "</methodCall>";

    private static final String template = "<param><value><string>{data}</string></value></param> \n";

    private static final String pingUrl = "http://ping.baidu.com/ping/RPC2";

    @Override
    public void ping(PingData data) {
        String dataString = Util.replace(xml, template, data);
        String respose = HttpUtil.httpPost(pingUrl, dataString);
        if (respose != null && respose.contains("<int>0</int>")) {
            //success
        } else {
            //error
            log.error("baidu ping error:" + respose);
        }
    }

    public static void main(String[] args) {
        BaiduPinger pinger = new BaiduPinger();
        PingData data = PingData.create("test", "http://www.baidu.com/detail");
        pinger.ping(data);
    }
}
