package io.jpress.commons.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import io.jboot.Jboot;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliyunLiveUtil {

    private static final String accessKeyId = Jboot.configValue("jboot.aliyun.live.accessKeyId");
    private static final String accessKeySecret = Jboot.configValue("jboot.aliyun.live.accessKeySecret");
    private static final String regionId = Jboot.configValue("jboot.aliyun.live.regionId", "cn-beijing");
    private static final String appName = Jboot.configValue("jboot.aliyun.live.appName");

    private static final String playDomain = Jboot.configValue("jboot.aliyun.live.playDomain");
    private static final String playAuthString = Jboot.configValue("jboot.aliyun.live.playAuthString");

    private static final String pushDomain = Jboot.configValue("jboot.aliyun.live.pushDomain");
    private static final String pushAuthString = Jboot.configValue("jboot.aliyun.live.pushAuthString");

    public static String getAppName() {
        return appName;
    }


    public static String getPlayDomain() {
        return playDomain;
    }


    /**
     * 创建 M3U8 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForM3U8(String streamName) {
        StringBuilder sb = new StringBuilder(playDomain);
        sb.append("/").append(appName).append("/").append(streamName).append(".m3u8");

        String key = playAuthString;                       // private key of authorization
        long exp = System.currentTimeMillis() / 1000 + 2 * 3600;  // expiration time: 2 hour after current time
        return appendAuthSign(sb.toString(), key, exp);                    // auth type:
    }

    /**
     * 创建 FLV 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForFLV(String streamName) {
        StringBuilder sb = new StringBuilder(playDomain);
        sb.append("/").append(appName).append("/").append(streamName).append(".flv");

        String key = playAuthString;                       // private key of authorization
        long exp = System.currentTimeMillis() / 1000 + 2 * 3600;  // expiration time: 2 hour after current time
        return appendAuthSign(sb.toString(), key, exp);                    // auth type:
    }


    /**
     * 创建 RTMP 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForRTMP(String streamName) {
        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(playDomain).append("/").append(appName).append("/").append(streamName);

        String key = playAuthString;                       // private key of authorization
        long exp = System.currentTimeMillis() / 1000 + 2 * 3600;  // expiration time: 2 hour after current time
        return appendAuthSign(sb.toString(), key, exp);                    // auth type:
    }


    /**
     * 生成推流地址
     *
     * @param streamName
     * @return
     */
    public static String createPushUrl(String streamName) {
        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(pushDomain).append("/").append(appName).append("/").append(streamName);

        String key = pushAuthString;                       // private key of authorization
        long exp = System.currentTimeMillis() / 1000 + 2 * 3600;  // expiration time: 2 hour after current time
        return appendAuthSign(sb.toString(), key, exp);                    // auth type:
    }


    /**
     * 生成授权码
     *
     * @param uri 原始URL地址
     * @param key 鉴权 key，阿里云生成
     * @param exp 超时时间
     * @return
     */
    private static String appendAuthSign(String uri, String key, long exp) {
        String pattern = "^(rtmp://|http://|https://)?([^/?]+)(/[^?]*)?(\\\\?.*)?$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(uri);
        String scheme = "", host = "", path = "", args = "";
        if (m.find()) {
            scheme = m.group(1) == null ? "rtmp://" : m.group(1);
            host = m.group(2) == null ? "" : m.group(2);
            path = m.group(3) == null ? "/" : m.group(3);
            args = m.group(4) == null ? "" : m.group(4);
        } else {
            System.out.println("NO MATCH");
        }

        String rand = "0";  // "0" by default, other value is ok
        String uid = "0";   // "0" by default, other value is ok
        String sString = String.format("%s-%s-%s-%s-%s", path, exp, rand, uid, key);
        String hashValue = md5Sum(sString);
        String authKey = String.format("%s-%s-%s-%s", exp, rand, uid, hashValue);
        if (args.isEmpty()) {
            return String.format("%s%s%s%s?auth_key=%s", scheme, host, path, args, authKey);
        } else {
            return String.format("%s%s%s%s&auth_key=%s", scheme, host, path, args, authKey);
        }
    }


    private static String md5Sum(String src) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(StandardCharsets.UTF_8.encode(src));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }


    private static IAcsClient createClient() throws Exception {
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        //DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "live", "live.aliyuncs.com"); //添加自定义endpoint
//        IAcsClient client = new DefaultAcsClient(profile);
        //System.setProperty("http.proxyHost", "127.0.0.1"); //用于设置代理，可用fiddler拦截查看HTTP请求，便于调试
        //System.setProperty("http.proxyPort", "8888");
        return new DefaultAcsClient(profile);
    }
}
