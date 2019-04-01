package io.jpress.web.seoping;

import io.jboot.utils.HttpUtil;

/**
 * 文档：https://ziyuan.baidu.com/linksubmit/index
 */
public class BaiduSeoProcesser {

    private static final String pushUrl = "http://data.zz.baidu.com/urls?site={site}&token={token}";
    private static final String updateUrl = "http://data.zz.baidu.com/update?site={site}&token={token}";

    public static void push(String site, String token, String... urls) {
        process(pushUrl, site, token, urls);
    }

    public static void update(String site, String token, String... urls) {
        process(updateUrl, site, token, urls);
    }


    private static void process(String postUrl, String site, String token, String... urls) {
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


    }


}
