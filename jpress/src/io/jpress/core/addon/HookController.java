package io.jpress.core.addon;

import com.jfinal.render.Render;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;

@UrlMapping(url = "/hook")
public class HookController extends JBaseController {

	public void index() {
		Render render = Hook.process_controller(this);
		if (null == render) {
			renderError(404);
			return;
		}

		render(render);
	}
}
