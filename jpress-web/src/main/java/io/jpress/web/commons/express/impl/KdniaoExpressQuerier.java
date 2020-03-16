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
package io.jpress.web.commons.express.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;
import com.jfinal.log.Log;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.web.commons.express.ExpressCompany;
import io.jpress.web.commons.express.ExpressInfo;
import io.jpress.web.commons.express.ExpressQuerier;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/20
 * <p>
 * http://www.kdniao.com/api-track
 */
public class KdniaoExpressQuerier implements ExpressQuerier {

    private static final Log LOG = Log.getLog(KdniaoExpressQuerier.class);

    @Override
    public List<ExpressInfo> query(ExpressCompany company, String num) {
        String appId = JPressOptions.get("express_api_appid");
        String appSecret = JPressOptions.get("express_api_appsecret");

        String requestData = "{'OrderCode':'','ShipperCode':'" + company.getCode() + "','LogisticCode':'" + num + "'}";

        Map<String, Object> params = new HashMap<>();
        try {
            params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
            params.put("EBusinessID", appId);
            params.put("RequestType", "1002");
            String dataSign = Base64Kit.encode(HashKit.md5(requestData + appSecret));
            params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
            params.put("DataType", "2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String result = HttpUtil.httpPost("http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx", params);
        if (StrUtil.isBlank(result)) {
            return null;
        }

        try {
            JSONObject object = JSONObject.parseObject(result);
            if (object.getBooleanValue("Success")) {
                JSONArray jsonArray = object.getJSONArray("Traces");
                if (jsonArray != null && jsonArray.size() > 0) {
                    List<ExpressInfo> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject expObject = jsonArray.getJSONObject(i);
                        ExpressInfo ei = new ExpressInfo();
                        ei.setInfo(expObject.getString("AcceptStation"));
                        ei.setTime(expObject.getString("AcceptTime"));
                        list.add(ei);
                    }
                    return list;
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        LOG.error(result);
        return null;
    }
}
