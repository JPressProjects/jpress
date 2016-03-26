package io.jpress.plugin.message.listener;

import java.util.Date;

import io.jpress.model.User;
import io.jpress.plugin.message.Message;
import io.jpress.plugin.message.MessageAction;
import io.jpress.plugin.message.MessageListener;

public class UserActionListener implements MessageListener {


	@Override
	public void onMessage(Message message) {
		if(message.getAction().equals(Actions.USER_LOGINED)){
			User user = message.getData();
			user.setLogged(new Date());
			user.update();
		}
	}


	@Override
	public void onRegisterAction(MessageAction messageAction) {
		messageAction.register(Actions.USER_LOGINED);
	}


}
