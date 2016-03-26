package io.jpress.plugin.message.listener;

import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class ContentListener implements MessageListener {


	@Override
	public void onMessage(Message message) {
		
	}


	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register("CONTENT_ADD");
	}


}
