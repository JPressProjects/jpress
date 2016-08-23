package io.jpress.addon.helloworld;

import io.jpress.menu.MenuGroup;
import io.jpress.menu.MenuItem;
import io.jpress.menu.MenuManager;
import io.jpress.message.Message;
import io.jpress.message.MessageListener;
import io.jpress.message.annotation.Listener;

@Listener(action = MenuManager.ACTION_INIT_MENU, async = false)
public class HelloMessage implements MessageListener {

	@Override
	public void onMessage(Message message) {

		MenuManager manager = message.getData();

		MenuGroup menuGroup = new MenuGroup("hello-test", null, "插件测试");

		MenuItem item = new MenuItem("test", "#", "插件测试");
		menuGroup.addMenuItem(item);

		manager.addMenuGroup(menuGroup);

	}

}
