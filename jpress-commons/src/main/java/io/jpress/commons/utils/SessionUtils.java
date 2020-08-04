package io.jpress.commons.utils;

import io.jboot.utils.CacheUtil;

/**
 * @author michael yang (fuhai999@gmail.com)
 */
public class SessionUtils {

    private static final String SESSION_NAME = "userSessions";
    private static final String SESSION_VALUE = "ok";

    public static void record(long userId) {
        CacheUtil.put(SESSION_NAME, userId, SESSION_VALUE);
    }

    public static void forget(long userId) {
        CacheUtil.remove(SESSION_NAME, userId);
    }


    public static boolean isLoginedOk(long userId) {
        return SESSION_VALUE.equals(CacheUtil.get(SESSION_NAME, userId));
    }
}
