package io.jpress.commons.qcloud;

import io.jpress.JPressOptions;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
     * 生成推流地址
     * @param streamName
     * @return
     * @throws Exception
     */
    public static String createPushUrl(String streamName) {
        String appName = JPressOptions.get("attachment_qcloudlive_appname");
        String pushDomain = JPressOptions.get("attachment_qcloudlive_pushdomain");

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = JPressOptions.get("attachment_qcloudlive_push_key");
        String txtime = JPressOptions.get("attachment_qcloudlive_txtime");

        if(appName == null){return "";}
        if(pushDomain == null){return "";}

        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(pushDomain).append("/").append(appName).append("/").append(streamName);

        if(key != null && !("").equals(key)){
            if(txtime != null && !("").equals(txtime)) {
                try {
                    long txTime = df.parse(txtime).getTime()/1000;
                    //播放域名的 Key鉴权 开启的情况
                    return sb.append(getSafeUrl(key,streamName,txTime)).toString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
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

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = JPressOptions.get("attachment_qcloudlive_play_key");
        String txtime = JPressOptions.get("attachment_qcloudlive_play_txtime");

        if(appName == null){return "";}
        if(playDomain == null){return "";}

        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(playDomain).append("/").append(appName).append("/").append(streamName).append(".m3u8");
        if(key != null && !("").equals(key)){
            if(txtime != null && !("").equals(txtime)) {
                try {
                    long txTime = df.parse(txtime).getTime()/1000;
                    //播放域名的 Key鉴权 开启的情况
                    return sb.append(getSafeUrl(key,streamName,txTime)).toString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    /**
     * 创建 Flv 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForFlv(String streamName) {
        String appName = JPressOptions.get("attachment_qcloudlive_appname");
        String playDomain = JPressOptions.get("attachment_qcloudlive_playdomain");

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = JPressOptions.get("attachment_qcloudlive_play_key");
        String txtime = JPressOptions.get("attachment_qcloudlive_play_txtime");

        if(appName == null){return "";}
        if(playDomain == null){return "";}

        StringBuilder sb = new StringBuilder("http://");
        sb.append(playDomain).append("/").append(appName).append("/").append(streamName).append(".flv");

        if(key != null && !("").equals(key)){
            if(txtime != null && !("").equals(txtime)) {
                try {
                    long txTime = df.parse(txtime).getTime()/1000;
                    //播放域名的 Key鉴权 开启的情况
                    return sb.append(getSafeUrl(key,streamName,txTime)).toString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
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

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = JPressOptions.get("attachment_qcloudlive_play_key");
        String txtime = JPressOptions.get("attachment_qcloudlive_play_txtime");

        if(appName == null){return "";}
        if(playDomain == null){return "";}

        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(playDomain).append("/").append(appName).append("/").append(streamName);

        if(key != null && !("").equals(key)){
            if(txtime != null && !("").equals(txtime)) {
                try {
                    long txTime = df.parse(txtime).getTime()/1000;
                    //播放域名的 Key鉴权 开启的情况
                    return sb.append(getSafeUrl(key,streamName,txTime)).toString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    /**
     * txSecret
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
                        append("?txSecret=").
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
