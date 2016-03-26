package io.jpress.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String now(){
		return sdf.format(new Date());
	}
	
	public static String format(Date date){
		if(null == date)
			return null;
		
		return sdf.format(date);
	}

}
