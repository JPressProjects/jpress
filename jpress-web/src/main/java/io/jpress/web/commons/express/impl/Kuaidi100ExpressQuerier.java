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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HashKit;
import com.jfinal.log.Log;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.web.commons.express.ExpressCompany;
import io.jpress.web.commons.express.ExpressInfo;
import io.jpress.web.commons.express.ExpressQuerier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/20
 * <p>
 * https://www.kuaidi100.com/openapi/api_post.shtml
 */
public class Kuaidi100ExpressQuerier implements ExpressQuerier {


    private static final Log LOG = Log.getLog(Kuaidi100ExpressQuerier.class);


    @Override
    public List<ExpressInfo> query(ExpressCompany company, String num) {

        String appId = JPressOptions.get("express_api_appid");
        String appSecret = JPressOptions.get("express_api_appsecret");

        String param = "{\"com\":\"" + company.getCode() + "\",\"num\":\"" + num + "\"}";
        String sign = HashKit.md5(param + appSecret + appId).toUpperCase();
        HashMap params = new HashMap();
        params.put("param", param);
        params.put("sign", sign);
        params.put("customer", appId);


        String result = HttpUtil.httpPost("http://poll.kuaidi100.com/poll/query.do", params);
        if (StrUtil.isBlank(result)) {
            return null;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (jsonArray != null && jsonArray.size() > 0) {
                List<ExpressInfo> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject expObject = jsonArray.getJSONObject(i);
                    ExpressInfo ei = new ExpressInfo();
                    ei.setInfo(expObject.getString("context"));
                    ei.setTime(expObject.getString("time"));
                    list.add(ei);
                }
                return list;
            }
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        LOG.error(result);
        return null;
    }
}
