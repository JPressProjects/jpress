package io.jpress.controller.front;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Option;

@UrlMapping(url = "/api")
public class ApiController extends JBaseController {

	public void index() {

		Boolean isOpen = Option.findValueAsBool("api_open");
		if (isOpen == null || isOpen == false) {
			renderAjaxResult("api is not open", 1);
			return;
		}
	}

}
