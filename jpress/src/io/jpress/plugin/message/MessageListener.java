package io.jpress.plugin.message;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	
	public void onMessage(Message message);
	public void onRegisterAction(MessageAction messageAction);

}
