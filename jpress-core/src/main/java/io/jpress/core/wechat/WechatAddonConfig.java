package io.jpress.core.wechat;

import java.lang.annotation.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.wechat
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WechatAddonConfig {

    String id();

    String title();

    String description();

    String author() default "";

    String authorWebsite() default "";

    String version() default "v1.0.0";

    String updateUrl() default "";

    int versionCode() default 0;

}
