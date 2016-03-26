package io.jpress.core.ui;

import java.util.List;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public abstract class JFunction implements TemplateMethodModelEx {
	
	private List<?> argList;

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List args) throws TemplateModelException {
		
		argList = args;
		
		return onExec();
	}
	
	public abstract Object onExec();
	
	
	public Object get(int index){
		if(null == argList || argList.size() ==0 )
			return null;
		
		if(index > argList.size()-1)
			return null;
		
		Object obj = argList.get(index);
		if(obj instanceof BeanModel){
			return  ((BeanModel)obj).getWrappedObject();
		}
		
		return null;
	}
	
	
	
	public StringModel getToStringModel(int index){
		if(null == argList || argList.size() ==0 )
			return null;
		
		if(index > argList.size()-1)
			return null;
		
		return (StringModel)argList.get(index);
	}
	

	public String getToString(int index){
		if(null == argList || argList.size() ==0 )
			return null;
		
		if(index > argList.size()-1)
			return null;
		
		return argList.get(index).toString();
	}
	
	
	public String getToString(int index,String defaultValue){
		if(null == argList || argList.size() ==0 )
			return defaultValue;
		
		if(index > argList.size()-1)
			return defaultValue;
		
		return argList.get(index).toString();
	}
	
	
	public Long getToLong(int index){
		
		String stringValue = getToString(index);
		
		if(null == stringValue || "".equals(stringValue.trim())){
			return null;
		}
		
		return Long.parseLong(stringValue);
	}
	
	
	public Long getToLong(int index,long defaultValue){
		String stringValue = getToString(index);
		
		if(null == stringValue){
			return defaultValue;
		}
		
		return Long.parseLong(stringValue);
	}

}
