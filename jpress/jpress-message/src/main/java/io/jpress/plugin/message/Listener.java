package io.jpress.plugin.message;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Listener {

	public static final int DEFAULT_WEIGHT = 10;

	int weight() default DEFAULT_WEIGHT;

	boolean async() default true;

	String[] action();
}