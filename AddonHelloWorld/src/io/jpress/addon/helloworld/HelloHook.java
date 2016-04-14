package io.jpress.addon.helloworld;

import com.jfinal.render.Render;
import com.jfinal.render.TextRender;

import io.jpress.core.addon.Hook;
import io.jpress.core.addon.HookController;

public class HelloHook extends Hook {

	public Render process_controller(HookController controller) {
		return new TextRender("hello addon");
	}

}
