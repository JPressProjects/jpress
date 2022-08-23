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
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.tms.v20200713.TmsClient;
import com.tencentcloudapi.tms.v20200713.models.TextModerationRequest;
import com.tencentcloudapi.tms.v20200713.models.TextModerationResponse;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class CloudWordFilterUtil {



    /**
     * 阿里云 文本内容检测
     * @param accessKeyId
     * @param secret
     * @param content
     */
    public static boolean aliyunTextScan(String accessKeyId,String secret,String regionId,String content) {

        IClientProfile profile = DefaultProfile
                .getProfile(regionId, accessKeyId, secret);
        DefaultProfile
                .addEndpoint(regionId, "Green", "green.cn-shanghai.aliyuncs.com");
        IAcsClient client = new DefaultAcsClient(profile);
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 指定API返回格式。
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(com.aliyuncs.http.MethodType.POST); // 指定请求方法。
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId(regionId);
        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符。
         */
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * 检测场景。文本垃圾检测请传递antispam。
         **/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);

        textScanRequest.setHttpContent(data.toJSONString().getBytes(StandardCharsets.UTF_8), "UTF-8", FormatType.JSON);
        // 请务必设置超时时间。
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if(httpResponse.isSuccess()){
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));

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

                                if(("block").equals(suggestion)){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }

        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }



    /**
     * 小花儿AI 文本内容检测
     * @param appCode
     * @param content
     */
    public static boolean xiaohuaeraiTextScan(String appCode , String content){
        String host = "https://textfilter.xiaohuaerai.com";
        String path = "/textfilter";
        String method = "POST";
        String appcode = appCode;
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, Object> bodys = new HashMap<String, Object>();
        bodys.put("src", content);
        bodys.put("type", "detail");
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
//            获取response的body
//            System.out.println(EntityUtils.toString(response.getEntity()));

            String response = HttpUtil.httpPost(host + path, bodys, headers, null);
            if(StrUtil.isNotBlank(response)){
                JSONObject jsonObject = JSONObject.parseObject(response);

                if(200 == jsonObject.getInteger("status") && ("block").equals(jsonObject.getString("msg"))){
                    //非法文本
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 泰岳语义 文本内容检测
     * @param appCode
     * @param content
     */

    public static boolean ultrapowerTextScan(String appCode , String content){
        String host = "http://monitoring.market.alicloudapi.com";
        String path = "/neirongjiance";
        String method = "POST";
        String appcode = appCode;
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, Object> bodys = new HashMap<String, Object>();
        bodys.put("in", content);


        try {

            String response = HttpUtil.httpPost(host + path, bodys, headers, null);
            if(StrUtil.isNotBlank(response)){
                JSONObject jsonObject = JSONObject.parseObject(response);

                if (200 == jsonObject.getInteger("status") && jsonObject.getBoolean("success")) {
                    JSONObject data = JSON.parseObject(jsonObject.getString("data"));
//                    System.out.println("data--->"+data);

                    JSONArray out = data.getJSONArray("out");

                    for (Object result : out) {

                        String politics = ((JSONObject)result).getString("政治敏感监测");
                        String politicsStr = politics.substring(politics.indexOf("[") + 1, politics.lastIndexOf("]"));
                        if(StrUtil.isNotBlank(politicsStr)){
                            return true;
                        }
                        String contraband = ((JSONObject)result).getString("违禁品监测");
                        String contrabandStr = contraband.substring(contraband.indexOf("[") + 1, contraband.lastIndexOf("]"));
                        if(StrUtil.isNotBlank(contrabandStr)){
                            return true;
                        }
                        String flood = ((JSONObject)result).getString("恶意灌水监测");//false

                        String porn = ((JSONObject)result).getString("色情监测");
                        String pornStr = porn.substring(porn.indexOf("[") + 1, porn.lastIndexOf("]"));
                        if(StrUtil.isNotBlank(pornStr)){
                            return true;
                        }
                        String abuse = ((JSONObject)result).getString("辱骂监测");
                        String abuseStr = abuse.substring(abuse.indexOf("[") + 1, abuse.lastIndexOf("]"));
                        if(StrUtil.isNotBlank(abuseStr)){
                            return true;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 腾讯云 文本内容安全
     * @param content
     * @return
     */
    public static boolean  qcloudTextScan(String accessKeyId,String secret,String region,String content){
        try{

            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(accessKeyId, secret);

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("tms.tencentcloudapi.com");

            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            // 实例化要请求产品的client对象,clientProfile是可选的
            TmsClient client = new TmsClient(cred, region, clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            TextModerationRequest req = new TextModerationRequest();

            String encodeToString =null;
            if(StrUtil.isNotBlank(content)){
                //Content表示待检测对象的文本内容，文本需要按utf-8格式编码，长度不能超过10000个字符（按unicode编码计算），并进行 Base64加密
                encodeToString = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
            }
            req.setContent(encodeToString);

            // 返回的resp是一个TextModerationResponse的实例，与请求对象对应
            TextModerationResponse resp = client.TextModeration(req);

            // 输出json格式的字符串回包
//            System.out.println(TextModerationResponse.toJsonString(resp));

            if(resp != null && ("Block").equals(resp.getSuggestion())){
                return true;
            }
        } catch (TencentCloudSDKException e) {
            e.toString();
        }
        return false;
    }

    /**
     * 判断文本内容是否 安全
     * @param content
     * @return
     */
    public static boolean isIllegalWords(String content){
        //开启云过滤功能
        String serviceEnable = JPressOptions.get("text_filter_service_enable");

        //云过滤服务商
        String service = JPressOptions.get("text_filter_service");

        String appId = JPressOptions.get("text_filter_appid");
        String appSecret = JPressOptions.get("text_filter_appsecret");
        String regionId = JPressOptions.get("text_filter_regionid");
        String appCode = JPressOptions.get("text_filter_appcode");

        if(StrUtil.isNotBlank(serviceEnable)){
            Boolean enable = Boolean.valueOf(serviceEnable);
            if(!enable){
                return false;
            }
        }


        if(StrUtil.isBlank(content)){
            return false;
        }


        if(("aliyun").equals(service)){//阿里云
            return aliyunTextScan(appId, appSecret,regionId, content);

        }
        else if(("qcloud").equals(service)){//腾讯云
            return qcloudTextScan(appId,appSecret,regionId,content);

        }
        else if(("xiaohuaerai").equals(service)){//小花儿AI
            return xiaohuaeraiTextScan(appCode,content);

        }
        else if(("ultrapower").equals(service)){//泰岳语义工厂
            return ultrapowerTextScan(appCode,content);

        }

        return false;
    }


}
