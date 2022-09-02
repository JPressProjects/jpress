//package io.jpress.commons.qcloud;
//
//import io.jboot.utils.HttpUtil;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//import java.util.TreeMap;
//
//public class QCloudApiUtil {
//
//    private final static Charset UTF8 = StandardCharsets.UTF_8;
//    private final static String SECRET_ID = "AKIDQuvluETMPB****";
//    private final static String SECRET_KEY = "wIxSwvXmc6k***";
//    private final static String CT_JSON = "application/json; charset=utf-8";
//
//    public static void main(String[] args) throws Exception {
//        String service = "sms";
//        String host = "sms.tencentcloudapi.com";
//        String region = "ap-beijing";
//        String action = "SendSms";
//        String version = "2021-01-11";
//        String timestamp = (System.currentTimeMillis()/1000)+"";
//
//        String payload = "{\n" +
//                "    \"PhoneNumberSet\": [\n" +
//                "        \"+86186********\"\n" +
//                "    ],\n" +
//                "    \"SmsSdkAppId\": \"1400152434\",\n" +
//                "    \"SignName\": \"JPress大本营\",\n" +
//                "    \"TemplateId\": \"215659\",\n" +
//                "    \"TemplateParamSet\": [\n" +
//                "        \"1234\",\n" +
//                "        \"5678\"\n" +
//                "    ],\n" +
//                "    \"SessionContext\": \"test\"\n" +
//                "}";
//
//        request(SECRET_ID,SECRET_KEY,service,host,region,action,version,timestamp,payload);
//    }
//
//    public static byte[] hmac256(byte[] key, String msg) throws Exception {
//        Mac mac = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
//        mac.init(secretKeySpec);
//        return mac.doFinal(msg.getBytes(UTF8));
//    }
//
//    public static String sha256Hex(String s) throws Exception {
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        byte[] d = md.digest(s.getBytes(UTF8));
//        return DatatypeConverter.printHexBinary(d).toLowerCase();
//    }
//
//    public static void request(String SECRET_ID,String SECRET_KEY, String service,String host,String region,String action,String version,String timestamp,String payload) throws Exception {
////        String service = "cvm";
////        String host = "cvm.tencentcloudapi.com";
////        String region = "ap-guangzhou";
////        String action = "DescribeInstances";
////        String version = "2017-03-12";
//        String algorithm = "TC3-HMAC-SHA256";
////        String timestamp = "1551113065";
//
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        // 注意时区，否则容易出错
//        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String date = sdf.format(new Date(Long.parseLong(timestamp + "000")));
//
//        // ************* 步骤 1：拼接规范请求串 *************
//        String httpRequestMethod = "POST";
//        String canonicalUri = "/";
//        String canonicalQueryString = "";
//        String canonicalHeaders = "content-type:application/json; charset=utf-8\n" + "host:" + host + "\n";
//        String signedHeaders = "content-type;host";
//
////        String payload = "{\"Limit\": 1, \"Filters\": [{\"Values\": [\"\\u672a\\u547d\\u540d\"], \"Name\": \"instance-name\"}]}";
//        String hashedRequestPayload = sha256Hex(payload);
//        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
//                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;
//
//
//        // ************* 步骤 2：拼接待签名字符串 *************
//        String credentialScope = date + "/" + service + "/" + "tc3_request";
//        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
//        String stringToSign = algorithm + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;
//
//
//        // ************* 步骤 3：计算签名 *************
//        byte[] secretDate = hmac256(("TC3" + SECRET_KEY).getBytes(UTF8), date);
//        byte[] secretService = hmac256(secretDate, service);
//        byte[] secretSigning = hmac256(secretService, "tc3_request");
//        String signature = DatatypeConverter.printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();
//
//
//        // ************* 步骤 4：拼接 Authorization *************
//        String authorization = algorithm + " " + "Credential=" + SECRET_ID + "/" + credentialScope + ", "
//                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;
//
//        TreeMap<String, String> headers = new TreeMap<>();
//        headers.put("Authorization", authorization);
//        headers.put("Content-Type", CT_JSON);
//        headers.put("Host", host);
//        headers.put("X-TC-Action", action);
//        headers.put("X-TC-Timestamp", timestamp);
//        headers.put("X-TC-Version", version);
//        headers.put("X-TC-Region", region);
//
////        StringBuilder sb = new StringBuilder();
////        sb.append("curl -X POST https://").append(host)
////                .append(" -H \"Authorization: ").append(authorization).append("\"")
////                .append(" -H \"Content-Type: application/json; charset=utf-8\"")
////                .append(" -H \"Host: ").append(host).append("\"")
////                .append(" -H \"X-TC-Action: ").append(action).append("\"")
////                .append(" -H \"X-TC-Timestamp: ").append(timestamp).append("\"")
////                .append(" -H \"X-TC-Version: ").append(version).append("\"")
////                .append(" -H \"X-TC-Region: ").append(region).append("\"")
////                .append(" -d '").append(payload).append("'");
////        System.out.println(sb.toString());
//
//        String post = HttpUtil.httpPost("https://" + host, null, headers, payload);
//        System.out.println(post);
//    }
//}
