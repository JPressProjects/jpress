package io.jpress.commons.qcloud;

import io.jboot.utils.StrUtil;
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
        return JPressOptions.get("attachment_qcloudlive_appname","");
    }


    public static String getPlayDomain() {
        return JPressOptions.get("attachment_qcloudlive_playdomain","");
    }

    public static String getPushDomain() {
        return JPressOptions.get("attachment_qcloudlive_pushdomain","");
    }


    public static String getPlayKey() {
        return JPressOptions.get("attachment_qcloudlive_play_key","");
    }

    public static String getPlayTxTime() {
        return JPressOptions.get("attachment_qcloudlive_play_txtime","");
    }

    public static String getPushKey() {
        return JPressOptions.get("attachment_qcloudlive_push_key","");
    }

    public static String getPushTxTime() {
        return JPressOptions.get("attachment_qcloudlive_txtime","");
    }


    /**
     * 生成推流地址
     * @param streamName
     * @return
     * @throws Exception
     */
    public static String createPushUrl(String streamName) {

        if(StrUtil.isAnyBlank(getPushDomain(),getAppName())){
            return "";
        }

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = getPushKey();
        String txtime = getPushTxTime();

        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(getPushDomain()).append("/").append(getAppName()).append("/").append(streamName);

        if(StrUtil.isNotBlank(key)){
            if(StrUtil.isNotBlank(txtime)) {
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
     * 创建 M3U8格式 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForM3U8(String streamName) {

        if(StrUtil.isAnyBlank(getPlayDomain(),getAppName())){
            return "";
        }

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = getPlayKey();
        String txtime = getPlayTxTime();

        StringBuilder sb = new StringBuilder("http://");

        sb.append(getPlayDomain()).append("/").append(getAppName()).append("/").append(streamName).append(".m3u8");
        if(StrUtil.isNotBlank(key)){
            if(StrUtil.isNotBlank(txtime)) {
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
     * 创建 Flv格式 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForFlv(String streamName) {

        if(StrUtil.isAnyBlank(getPlayDomain(),getAppName())){
            return "";
        }

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = getPlayKey();
        String txtime = getPlayTxTime();

        StringBuilder sb = new StringBuilder("http://");
        sb.append(getPlayDomain()).append("/").append(getAppName()).append("/").append(streamName).append(".flv");

        if(StrUtil.isNotBlank(key)){
            if(StrUtil.isNotBlank(txtime)) {
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
     * 创建 RTMP格式 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForRTMP(String streamName) {

        if(StrUtil.isAnyBlank(getPlayDomain(),getAppName())){
            return "";
        }

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = getPlayKey();
        String txtime = getPlayTxTime();

        StringBuilder sb = new StringBuilder("rtmp://");
        sb.append(getPlayDomain()).append("/").append(getAppName()).append("/").append(streamName);

        if(StrUtil.isNotBlank(key)){
            if(StrUtil.isNotBlank(txtime)) {
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
     * 创建 UDP格式 的播放地址
     *
     * @param streamName
     * @return
     */
    public static String createPlayUrlForUDP(String streamName) {

        if(StrUtil.isAnyBlank(getPlayDomain(),getAppName())){
            return "";
        }

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = getPlayKey();
        String txtime = getPlayTxTime();

        StringBuilder sb = new StringBuilder("webrtc://");
        sb.append(getPlayDomain()).append("/").append(getAppName()).append("/").append(streamName);

        if(StrUtil.isNotBlank(key)){
            if(StrUtil.isNotBlank(txtime)) {
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
