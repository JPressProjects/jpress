package io.jpress.core.addon;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Hooks {
	
	private Map<String , Method> hooks = new HashMap<String, Method>();
	
	public void register(String hookName,String className,String methodName){
		hooks.put(hookName, null);
	}
	
	public Method hook(String hookName){
		return hooks.get(hookName);
	}

}
