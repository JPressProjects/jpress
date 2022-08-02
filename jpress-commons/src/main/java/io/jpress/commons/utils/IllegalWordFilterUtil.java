package io.jpress.commons.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.imageaudit.model.v20191230.ScanTextRequest;
import com.aliyuncs.imageaudit.model.v20191230.ScanTextResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tms.v20200713.TmsClient;
import com.tencentcloudapi.tms.v20200713.models.TextModerationRequest;
import com.tencentcloudapi.tms.v20200713.models.TextModerationResponse;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class IllegalWordFilterUtil {

    /**
     * 阿里云 文本内容检测
     * @param Content
     */
    public static void textScanRequest(String Content) {

        IClientProfile profile = DefaultProfile
                .getProfile("cn-shanghai", "", "");
        DefaultProfile
                .addEndpoint("cn-shanghai", "Green", "green.cn-shanghai.aliyuncs.com");
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 指定API返回格式。
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法。
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符。
         */
        task1.put("content", "我所有的朋友，都普遍聪明平和，去你妈的，大傻逼，就像是一种社交工具，你在吸的时候可以谈很深刻的事情，而如果你在清醒的时候谈，别人一定会觉得你有病。");
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * 检测场景。文本垃圾检测请传递antispam。
         **/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        System.out.println(JSON.toJSONString(data, true));
        textScanRequest.setHttpContent(data.toJSONString().getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);
        // 请务必设置超时时间。
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if(httpResponse.isSuccess()){
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));
                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if(200 == ((JSONObject)taskResult).getInteger("code")){
                            JSONArray sceneResults = ((JSONObject)taskResult).getJSONArray("results");
                            for (Object sceneResult : sceneResults) {
                                String scene = ((JSONObject)sceneResult).getString("scene");
                                String suggestion = ((JSONObject)sceneResult).getString("suggestion");
                                // 根据scene和suggetion做相关处理。
                                // suggestion为pass表示未命中垃圾。suggestion为block表示命中了垃圾，可以通过label字段查看命中的垃圾分类。
                                System.out.println("args = [" + scene + "]");
                                System.out.println("args = [" + suggestion + "]");
                            }
                        }else{
                            System.out.println("task process fail:" + ((JSONObject)taskResult).getInteger("code"));
                        }
                    }
                } else {
                    System.out.println("detect not success. code:" + scrResponse.getInteger("code"));
                }
            }else{
                System.out.println("response not success. status:" + httpResponse.getStatus());
            }
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void scanText(String content){
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", "<your-access-key-id>", "<your-access-key-secret>");

        IAcsClient client = new DefaultAcsClient(profile);

        ScanTextRequest request = new ScanTextRequest();

        List<ScanTextRequest.Tasks> tasksList = new ArrayList<ScanTextRequest.Tasks>();

        ScanTextRequest.Tasks tasks1 = new ScanTextRequest.Tasks();
        tasks1.setContent("");
        tasksList.add(tasks1);
        request.setTaskss(tasksList);

        List<ScanTextRequest.Labels> labelsList = new ArrayList<ScanTextRequest.Labels>();

        ScanTextRequest.Labels labels1 = new ScanTextRequest.Labels();
        labels1.setLabel("abuse");
        labelsList.add(labels1);

        ScanTextRequest.Labels labels2 = new ScanTextRequest.Labels();
        labels2.setLabel("contraband");
        labelsList.add(labels2);

        ScanTextRequest.Labels labels3 = new ScanTextRequest.Labels();
        labels3.setLabel("porn");
        labelsList.add(labels3);
        request.setLabelss(labelsList);

        try {
            ScanTextResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
//            System.out.println("ErrCode:" + e.getErrCode());
//            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        } catch (com.aliyuncs.exceptions.ClientException e) {
            e.printStackTrace();
        }
    }


    /**
     * 小花儿AI 文本内容检测
     * @param appCode
     * @param content
     */
    public static void textScanByAppCode(String appCode , String content){
        String host = "https://textfilter.xiaohuaerai.com";
        String path = "/textfilter";
        String method = "POST";
        String appcode = "你自己的AppCode";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("src", "需要过滤的文本");
        bodys.put("type", "strict");
        //strict:严格，easy：宽松，detail：详细信息


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//            System.out.println(response.toString());


            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 泰岳语义 文本内容检测
     * @param appCode
     * @param content
     */

    public static void textScanByTaiYue(String appCode , String content){
        String host = "http://monitoring.market.alicloudapi.com";
        String path = "/neirongjiance";
        String method = "POST";
        String appcode = "你自己的AppCode";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("in", "要过滤的文本");


        try {
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//            System.out.println(response.toString());


            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 腾讯云 文本内容安全
     * @param content
     * @return
     */
    public static String  qcloudTextScan(String content){
        try{

            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential("", "");

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tms.tencentcloudapi.com");

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TmsClient client = new TmsClient(cred, "ap-shanghai", clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            TextModerationRequest req = new TextModerationRequest();

            String encodeToString =null;
            if(content != null && !("").equals(content)){
                //Content表示待检测对象的文本内容，文本需要按utf-8格式编码，长度不能超过10000个字符（按unicode编码计算），并进行 Base64加密
                encodeToString = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
            }
            req.setContent(encodeToString);

            // 返回的resp是一个TextModerationResponse的实例，与请求对象对应
            TextModerationResponse resp = client.TextModeration(req);

            // 输出json格式的字符串回包
            System.out.println(TextModerationResponse.toJsonString(resp));

            return  TextModerationResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {

            System.out.println(e.toString());
           return e.toString();
        }
    }





}
