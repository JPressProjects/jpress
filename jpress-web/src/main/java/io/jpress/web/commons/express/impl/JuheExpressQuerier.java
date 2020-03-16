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
import java.util.Map;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/20
 * <p>
 * 文档
 * https://www.juhe.cn/docs/api/id/43
 * https://code.juhe.cn/docs/780
 */
public class JuheExpressQuerier implements ExpressQuerier {

    private static final Log LOG = Log.getLog(JuheExpressQuerier.class);

    private static Map<String, String> codeMap = new HashMap<>();

    static {
        codeMap.put("shunfeng", "sf");
        codeMap.put("yuantong", "yt");
        codeMap.put("zhongtong", "zto");
        codeMap.put("shentong", "sto");
        codeMap.put("yunda", "yd");
        codeMap.put("zhaijisong", "zjs");
        codeMap.put("ems", "ems");
        codeMap.put("youzheng", "yzgn");
        codeMap.put("ups", "ups");
        codeMap.put("fedex", "fedex");
        codeMap.put("shunda", "shunda");
        codeMap.put("debang", "db");
        codeMap.put("baishi", "ht");
        codeMap.put("jingdong", "jd");
    }

    @Override
    public List<ExpressInfo> query(ExpressCompany company, String num) {

        String appId = JPressOptions.get("express_api_appid");
        String appSecret = JPressOptions.get("express_api_appsecret");

        Map params = new HashMap();
        params.put("com", codeMap.get(company.getCode()));
        params.put("no", num);
        params.put("key", appId);
        params.put("dtype", "json");

        String result = HttpUtil.httpGet("http://v.juhe.cn/exp/index", params);
        if (StrUtil.isBlank(result)) {
            return null;
        }

        try {
            JSONObject object = JSONObject.parseObject(result);
            Integer errorCode = object.getInteger("error_code");
            if (errorCode != null && errorCode == 0) {
                JSONObject resultObject = object.getJSONObject("result");
                if (resultObject != null) {
                    JSONArray jsonArray = resultObject.getJSONArray("list");
                    if (jsonArray != null && jsonArray.size() > 0) {
                        List<ExpressInfo> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JSONObject expObject = jsonArray.getJSONObject(i);
                            ExpressInfo ei = new ExpressInfo();
                            ei.setInfo(expObject.getString("remark"));
                            ei.setTime(expObject.getString("datetime"));
                            list.add(ei);
                        }
                        return list;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.toString(), ex);
        }

        LOG.error(result);
        return null;
    }
}
