package io.jpress.service;

import io.jboot.Jboot;
import io.jpress.JPressConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.service
 */
public class Services {

    private static JPressConfig config = Jboot.config(JPressConfig.class);

    public static <T> T get(Class<T> clazz) {
        return config.isRpcEnable() ? Jboot.service(clazz) : Jboot.bean(clazz);
    }
}
