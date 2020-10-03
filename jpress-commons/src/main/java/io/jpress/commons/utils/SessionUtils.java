package io.jpress.commons.utils;

import io.jboot.Jboot;
import io.jboot.utils.CacheUtil;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class SessionUtils {

    private static final String SESSION_NAME = "userSessions";
    private static final String SESSION_VALUE = "ok";

    public static void record(Object userId) {
        CacheUtil.put(SESSION_NAME, String.valueOf(userId), SESSION_VALUE);
    }

    public static void forget(Object userId) {
        CacheUtil.remove(SESSION_NAME, String.valueOf(userId));
    }


    public static boolean isLoginedOk(Object userId) {
        // 开发模式下，不通过 Session 控制，否则使用内存缓存时，每次重启都需要重新登录
        return Jboot.isDevMode() || SESSION_VALUE.equals(CacheUtil.get(SESSION_NAME, String.valueOf(userId)));
    }
}
