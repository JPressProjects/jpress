package io.jpress.admin.menu.annotation;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AdminMenu {

    String text();

    String icon() default "";

    String target() default "";

    String flag() default "";

    int desc_no() default 0;
}