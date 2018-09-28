package io.jpress.core.menu.annotation;

import java.lang.annotation.*;

/**
 * 用来给给Controller的方法进行标注，申明此方法为一个用户中心的菜单
 * <p>
 * groupId 用来标识 这个方法被放在哪个group里
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface UCenterMenu {

    String groupId();

    String text();

    String icon() default "";

    String target() default "";


    int order() default 100; //越小在越前面
}