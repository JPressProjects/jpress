package io.jpress.plugin.target.converter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jpress.plugin.target.ItargetConverter;

public class ContentTargetConverter implements ItargetConverter {

	@Override
	public boolean match(String target) {
		
		return false;
	}

	@Override
	public String converter(String target, HttpServletRequest request,
			HttpServletResponse response) {
		
		
		return "/";
	}

}
