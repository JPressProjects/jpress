package io.jpress;

/**
 * 常量信息
 */
public class Constants {

    /**
     * 自定义的事件action
     */
    public static class Actions {
        public static final String USER_LOGINED = "user:logined";
    }


    /**
     * cookie信息
     */
    public static class Cookies {
        public static final String USER_ID = "user.id";
    }


    /**
     * 错误码
     */
    public static class ErrorCode {
        public static final int USER_NOT_EXIST = 1;
    }


    /**
     * 配置 信息
     */
    public static class OPTION {
        public static final String FAKE_STATIC_ENABLE = "fake_static_enable";
        public static final String FAKE_STATIC_POSTFIX = "fake_static_postfix";
    }
}
