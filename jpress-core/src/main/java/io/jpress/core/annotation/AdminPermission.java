package io.jpress.core.annotation;

import java.lang.annotation.*;

/**
 * AdminPermission
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AdminPermission {

    String value();

}