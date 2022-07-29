package io.jpress.commons;

import io.jboot.utils.StrUtil;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.util.Map;

public class UserAgentUtil {

    private static UserAgentAnalyzer uaa = UserAgentAnalyzer
            .newBuilder()
            .build();


    public static Map<String, String> getUserAgent(String userAgentString) {
        if (StrUtil.isBlank(userAgentString)) {
            return null;
        }
        return uaa.parse(userAgentString).toMap();
    }


    /**
     * 浏览器名称
     *
     * @return
     */
    public static String getBrowserName(Map<String, String> userAgent) {
        return userAgent != null ? userAgent.get("AgentName") : null;
    }


    /**
     * 获取浏览器版本
     *
     * @return
     */
    public static String getBrowserVersion(Map<String, String> userAgent) {
        return userAgent != null ? userAgent.get("AgentVersion") : null;
    }

    /**
     * 获取操作系统名称
     *
     * @return
     */
    public static String getOsName(Map<String, String> userAgent) {
        return userAgent != null ? userAgent.get("OperatingSystemName") : null;
    }


    /**
     * 获取设备名称
     *
     * @return
     */
    public static String getDeviceName(Map<String, String> userAgent) {
        return userAgent != null ? userAgent.get("DeviceName") : null;
    }

    /**
     * 获取设备厂商
     *
     * @return
     */
    public static String getDeviceBrand(Map<String, String> userAgent) {
        return userAgent != null ? userAgent.get("DeviceBrand") : null;
    }


    /**
     * 获取网络类型
     *
     * @return
     */
    public static String getNetworkType(Map<String, String> userAgent) {
        return userAgent != null ? userAgent.get("NetworkType") : null;
    }
}
