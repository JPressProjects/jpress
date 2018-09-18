package io.jpress.core.annotation;

import java.lang.annotation.*;

/**
 * 用在API上，标准为需要签名进行认证，才能正常使用该API
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NeedAuthentication {
}