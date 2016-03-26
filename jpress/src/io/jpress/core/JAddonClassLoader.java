package io.jpress.core;

public class JAddonClassLoader extends ClassLoader {
	
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		
		//defineClass(name, b, protectionDomain)
		
		return super.findClass(name);
	}

}
