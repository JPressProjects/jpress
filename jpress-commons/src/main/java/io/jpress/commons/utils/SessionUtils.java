package io.jpress.commons.utils;

import io.jboot.Jboot;
import io.jboot.utils.CacheUtil;
import io.jboot.utils.StrUtil;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class SessionUtils {

    private static final String SESSION_NAME = "user_sessions";

    public static void record(Object userId) {
        CacheUtil.put(SESSION_NAME, String.valueOf(userId), StrUtil.uuid());
    }

    public static void forget(Object userId) {
        CacheUtil.remove(SESSION_NAME, String.valueOf(userId));
    }

    public static String getSessionId(Object userId) {
        return  CacheUtil.get(SESSION_NAME, String.valueOf(userId));
    }

    public static boolean isLoginedOk(Object userId) {
        // 开发模式下，不通过 Session 控制，否则因为使用内存缓存，每次重启都需要重新登录，影响开发体验
        return  Jboot.isDevMode() || StrUtil.isNotBlank(getSessionId(userId));
    }
}
