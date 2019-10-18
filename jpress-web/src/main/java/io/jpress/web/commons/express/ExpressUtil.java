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
package io.jpress.web.commons.express;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HashKit;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressUtil {

    /**
     * 快递查询
     *
     * @param expressCom 快递公司
     * @param num        快递单号
     * @return
     */
    public static List<ExpressInfo> queryExpress(ExpressCom expressCom, String num) {
        String type = JPressOptions.get("express_api_type");
        String appId = JPressOptions.get("express_api_appid");
        String appSecret = JPressOptions.get("express_api_appsecret");
        switch (type) {
            case "kuaidi100":
                return queryKuaidi100(appId, appSecret, expressCom, num);
            case "juhecn":
                return queryJuhe(appId, appSecret, expressCom, num);
            case "kdniao":
                return queryKdniao(appId, appSecret, expressCom, num);
            case "showapi":
                return queryShowapi(appId, appSecret, expressCom, num);
        }
        return null;
    }

    /**
     * https://www.kuaidi100.com/openapi/api_post.shtml
     *
     * @param appId
     * @param appKey
     * @param expressCom
     * @param num
     */
    private static List<ExpressInfo> queryKuaidi100(String appId, String appKey, ExpressCom expressCom, String num) {
        String param = "{\"com\":\"" + expressCom.getCode() + "\",\"num\":\"" + num + "\"}";
        String sign = HashKit.md5(param + appKey + appId);
        HashMap params = new HashMap();
        params.put("param", param);
        params.put("sign", sign);
        params.put("customer", appId);
        String resp = HttpUtil.httpPost("http://poll.kuaidi100.com/poll/query.do", params);

        return null;
    }

    /**
     * https://www.juhe.cn/docs/api/id/43
     * https://code.juhe.cn/docs/780
     *
     * @param appId
     * @param appKey
     * @param expressCom
     * @param num
     */
    private static List<ExpressInfo> queryJuhe(String appId, String appKey, ExpressCom expressCom, String num) {

        String url = "http://v.juhe.cn/exp/index";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("com", expressCom.getCode());//需要查询的快递公司编号
        params.put("no", num);//需要查询的订单号
        params.put("key", appKey);//应用APPKEY(应用详细页查询)
        params.put("dtype", "json");//返回数据的格式,xml或json，默认json

        String result = null;
        try {
            result = HttpUtil.httpGet(url, params);
            JSONObject object = JSONObject.parseObject(result);
            if (object.getInteger("error_code") == 0) {
                System.out.println(object.get("result"));
            } else {
                System.out.println(object.get("error_code") + ":" + object.get("reason"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * http://www.kdniao.com/api-track
     *
     * @param appId
     * @param appKey
     * @param expressCom
     * @param num
     */
    private static List<ExpressInfo> queryKdniao(String appId, String appKey, ExpressCom expressCom, String num) {
        String requestData = "{'OrderCode':'','ShipperCode':'" + expressCom.getCode() + "','LogisticCode':'" + num + "'}";

        Map<String, Object> params = new HashMap<>();
        try {
            params.put("RequestData", URLEncoder.encode(requestData, "UTF-8"));
            params.put("EBusinessID", appId); //请到快递鸟官网申请http://www.kdniao.com/ServiceApply.aspx
            params.put("RequestType", "1002");
            String dataSign = Base64Kit.encode(HashKit.md5(requestData + appKey));//encrypt(requestData, appKey);
            params.put("DataSign", URLEncoder.encode(dataSign, "UTF-8"));
            params.put("DataType", "2");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String result = HttpUtil.httpPost("http://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx", params);

        //根据公司业务处理返回的信息......

        return null;
    }


    /**
     * https://www.showapi.com/apiGateway/view?apiCode=64
     *
     * @param appId
     * @param appKey
     * @param expressCom
     * @param num
     */
    private static List<ExpressInfo> queryShowapi(String appId, String appKey, ExpressCom expressCom, String num) {
        String param = "{\"com\":\"" + expressCom.getCode() + "\",\"num\":\"" + num + "\"}";
        String sign = HashKit.md5(param + appKey + appId);
        HashMap params = new HashMap();
        params.put("showapi_appid", appId);
        params.put("com", sign);
        params.put("nu", num);
        params.put("contentType", "bodyString");
        params.put("showapi_sign", signRequest(params, appKey));


        String resp = HttpUtil.httpGet("http://route.showapi.com/64-19", params);

        return null;
    }


    private static String signRequest(Map<String, Object> params, String appkey) {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            Object value = params.get(key);
            if (value != null && StrUtil.areNotEmpty(key, value.toString())) {
                builder.append(key).append(value);
            }
        }
        builder.append(appkey);
        return HashKit.md5(builder.toString());
    }
}
