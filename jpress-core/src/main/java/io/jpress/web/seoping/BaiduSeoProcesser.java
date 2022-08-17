package io.jpress.web.seoping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.LogKit;
import io.jboot.utils.HttpUtil;

/**
 * 文档：https://ziyuan.baidu.com/linksubmit/index
 */
public class BaiduSeoProcesser {

    private static final String pushUrl = "http://data.zz.baidu.com/urls?site={site}&token={token}";
    private static final String updateUrl = "http://data.zz.baidu.com/update?site={site}&token={token}";

    public static boolean push(String site, String token, String... urls) {
        return process(pushUrl, site, token, urls);
    }

    public static boolean update(String site, String token, String... urls) {
        return process(updateUrl, site, token, urls);
    }


    private static boolean process(String postUrl, String site, String token, String... urls) {
        StringBuilder pushData = new StringBuilder();
        for (String url : urls) {
            pushData.append(url).append("\n");
        }
        String url = postUrl.replace("{site}", site).replace("{token}", token);
        String response = HttpUtil.httpPost(url, pushData.toString());

        /**
         * response
         * 成功：
         * {
         *     "remain":4999998,
         *     "success":2,
         *     "not_same_site":[],
         *     "not_valid":[]
         * }
         *
         * 失败：
         * {
         *     "error":401,
         *     "message":"token is not valid"
         * }
         */

        if (response != null) {
            JSONObject json = JSON.parseObject(response);
            Integer successCount = json.getInteger("success");
            if (successCount != null && successCount > 0) {
                return true;
            }
            LogKit.error("push or update is error : " + response);
        }

        return false;
    }


    public static void main(String[] args) {
        String site = "www.jpress.cn";
        String token = "avsegfd8WTAVOo7Iv";

        boolean success = push(site, token, "http://www.jpress.cn/club");
        System.out.println("success : " + success);

    }


}
