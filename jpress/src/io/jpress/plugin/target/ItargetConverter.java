package io.jpress.plugin.target;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ItargetConverter {
	
	public  boolean match(String target);
	public  String converter(String target,HttpServletRequest request,HttpServletResponse response);
	

}
