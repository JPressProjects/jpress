package io.jpress.commons.utils;

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
        return SESSION_VALUE.equals(CacheUtil.get(SESSION_NAME, String.valueOf(userId)));
    }
}
