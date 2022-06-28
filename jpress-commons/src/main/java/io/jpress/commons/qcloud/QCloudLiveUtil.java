package io.jpress.commons.qcloud;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import io.jpress.JPressOptions;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QCloudLiveUtil {

    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getAppName() {
        return JPressOptions.get("attachment_qcloudlive_appname") == null
                || ("").equals(JPressOptions.get("attachment_qcloudlive_appname")) ?
                "": JPressOptions.get("attachment_qcloudlive_appname");
    }


    public static String getPlayDomain() {
        return JPressOptions.get("attachment_qcloudlive_playdomain") == null
                || ("").equals(JPressOptions.get("attachment_qcloudlive_playdomain")) ?
                "": JPressOptions.get("attachment_qcloudlive_playdomain");
    }


    /**
     * 创建 M3U8 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForM3U8(String streamName) {
        String appName = JPressOptions.get("attachment_qcloudlive_appname");
        String playDomain = JPressOptions.get("attachment_qcloudlive_playdomain");
        String playAuthString = JPressOptions.get("attachment_qcloudlive_playauthstring");

        if(appName == null){return "";}
        if(playDomain == null){return "";}
        if(playAuthString == null){return "";}

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
        String appName = JPressOptions.get("attachment_qcloudlive_appname");
        String playDomain = JPressOptions.get("attachment_qcloudlive_playdomain");
        String playAuthString = JPressOptions.get("attachment_qcloudlive_playauthstring");

        if(appName == null){return "";}
        if(playDomain == null){return "";}
        if(playAuthString == null){return "";}

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
        String appName = JPressOptions.get("attachment_qcloudlive_appname");
        String playDomain = JPressOptions.get("attachment_qcloudlive_playdomain");
        String playAuthString = JPressOptions.get("attachment_qcloudlive_playauthstring");

        if(appName == null){return "";}
        if(playDomain == null){return "";}
        if(playAuthString == null){return "";}

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
        String appName = JPressOptions.get("attachment_qcloudlive_appname");
        String pushDomain = JPressOptions.get("attachment_qcloudlive_pushdomain");
        String pushAuthString = JPressOptions.get("attachment_qcloudlive_pushauthstring");

        if(appName == null){return "";}
        if(pushDomain == null){return "";}
        if(pushAuthString == null){return "";}

        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(pushDomain).append("/").append(appName).append("/").append(streamName);
//        txSecret=Md5(key+StreamName+hex(time))&txTime=hex(time)
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
        String accessKeyId = JPressOptions.get("attachment_qcloudlive_accesskeyid");
        String accessKeySecret = JPressOptions.get("attachment_qcloudlive_accesskeysecret");
        String regionId = JPressOptions.get("attachment_qcloudlive_regionid");

        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        //DefaultProfile.addEndpoint("cn-shanghai", "cn-shanghai", "live", "live.aliyuncs.com"); //添加自定义endpoint
//        IAcsClient client = new DefaultAcsClient(profile);
        //System.setProperty("http.proxyHost", "127.0.0.1"); //用于设置代理，可用fiddler拦截查看HTTP请求，便于调试
        //System.setProperty("http.proxyPort", "8888");
        return new DefaultAcsClient(profile);
    }




    /**
     * 鉴权Key
     * KEY+ streamName + txTime
     */
    private static String getSafeUrl(String key, String streamName, long txTime) {
        String input = new StringBuilder().
                append(key).
                append(streamName).
                append(Long.toHexString(txTime).toUpperCase()).toString();

        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret  = byteArrayToHexString(
                    messageDigest.digest(input.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return txSecret == null ? "" :
                new StringBuilder().
                        append("txSecret=").
                        append(txSecret).
                        append("&").
                        append("txTime=").
                        append(Long.toHexString(txTime).toUpperCase()).
                        toString();
    }

    private static String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }



}
