package io.jpress.plugin.message.listener;

import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class SettingChangedListener implements MessageListener {


	@Override
	public void onMessage(Message message) {
		
		String [] keys = message.getData();
		
	}


	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.SETTING_CHANGED);
	}


}
