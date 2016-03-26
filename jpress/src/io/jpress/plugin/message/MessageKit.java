package io.jpress.plugin.message;

public class MessageKit {
	
	private static MessagePlugin messagePublisher;
	
	static void init(MessagePlugin publisher){
		messagePublisher = publisher;
	}
	
	public static void register(Class<? extends MessageListener> listenerClass) {
		messagePublisher.registerListener(listenerClass);
	}
	
	
	public static void sendMessage(Message message){
		messagePublisher.pulish(message);
	}
	
	public static void sendMessage(String action,Object data){
		messagePublisher.pulish(new Message(action,data));
	}
	

}
