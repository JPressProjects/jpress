package io.jpress.core.annotation;

import java.lang.annotation.*;

/**
 * 用在API上，标识需要用户登录才能正常使用该API
 * 一般情况使用在需要获得用户信息的API上，比如评论
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NeedUserLogined {
}