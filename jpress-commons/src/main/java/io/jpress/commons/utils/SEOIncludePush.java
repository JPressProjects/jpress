package io.jpress.commons.utils;

import com.google.common.collect.Maps;
import com.jfinal.aop.Aop;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * 搜索引擎收录推送工具类
 *
 * @author Eric.Huang
 * @date 2019-03-13 10:41
 * @package io.jpress.commons.utils
 **/

public class SEOIncludePush {

    public static final String BAIDU_URL = "http://data.zz.baidu.com/urls?site={site}&token={token}";

    /**
     * 百度收录主动推送
     * @author  Eric Huang
     * @date  2019-03-13 12:03
     * @param url       推送地址
     * @param params    推送线路链接
     * @return java.lang.String
     * @desc    
     */
    public static String baiduPush(String url, String[] params) {

        if (StrUtil.isBlank(url) || params == null || params.length == 0) {
            return null;
        }

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Host", "data.zz.baidu.com");
        headers.put("User-Agent", "curl/7.12.1");
        headers.put("Content-Length", "83");
        headers.put("Content-Type", "text/plain");

        String site = "";
        String token = "";
        String baiduUrl = BAIDU_URL.replace("{size}", site).replace("{token}", token);
        String postData = StringUtils.join(params, "\n");

        String result = HttpUtil.httpPost(baiduUrl, null, headers, postData);

        return result;
    }

    public static String _baiduPush(String url, String[] params) {

        if (null == url || null == params || params.length == 0) {
            return null;
        }

        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            //建立URL之间的连接
            URLConnection conn = new URL(url).openConnection();

            //设置通用的请求属性
            conn.setRequestProperty("Host", "data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //获取conn对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送请求参数
            String param = StringUtils.join(params, "\n");
            out.print(param.trim());
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
