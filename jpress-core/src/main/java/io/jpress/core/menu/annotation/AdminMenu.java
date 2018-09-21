package io.jpress.core.menu.annotation;

import java.lang.annotation.*;

/**
 * 用来给给Controller的方法进行标注，申明此方法为一个后台菜单
 * 后台菜单被包含在 group里，而 group 是由module来定义的，jpress系统也内置了几个group
 * <p>
 * groupId 用来标识 这个方法被放在哪个group里
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AdminMenu {

    String text();

    String icon() default "";

    String target() default "";

    String groupId();

    int order() default 100; //越小在越前面
}