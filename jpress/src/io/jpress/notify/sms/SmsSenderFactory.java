package io.jpress.notify.sms;

public class SmsSenderFactory {
	
	public static ISmsSender createSender(){
		
		return new SimplerSmsSender();
		
	}
	

}
