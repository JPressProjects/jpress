package io.jpress.core.menu.annotation;

import java.lang.annotation.*;

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