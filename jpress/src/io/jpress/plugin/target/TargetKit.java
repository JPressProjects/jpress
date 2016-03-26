package io.jpress.plugin.target;

import io.jpress.core.Jpress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TargetKit {

	static TargetConverterManager tcManager;

	static void init(TargetConverterManager tcm) {
		tcManager = tcm;
	}

	public static void register(Class<? extends ItargetConverter> clazz) {
		tcManager.register(clazz);
	}

	public static String converte(String target, HttpServletRequest request,HttpServletResponse response) {

		ItargetConverter converter = tcManager.match(target);
		if (null == converter) {
			return target;
		}

		String newTarget = converter.converter(target, request, response);

		if (Jpress.isDevMode()) {
			System.err.println(String.format(
					"target\"%s\" was converted to \"%s\" by %s.(%s.java:1)",
					target, newTarget, converter.getClass().getName(),
					converter.getClass().getSimpleName()));
		}

		return newTarget;

	}

}
