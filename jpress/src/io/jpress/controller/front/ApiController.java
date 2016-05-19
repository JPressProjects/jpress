package io.jpress.controller.front;

import java.math.BigInteger;
import java.util.List;

import com.jfinal.core.ActionKey;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Content;
import io.jpress.model.Option;

@UrlMapping(url = "/api")
public class ApiController extends JBaseController {

	public void index() {
		Boolean isOpen = Option.findValueAsBool("api_enable");
		if (isOpen == null || isOpen == false) {
			renderAjaxResult("api is not open", 1);
			return;
		}
	}

	@ActionKey("/admin/api")
	public void setting() {
		BigInteger id = getParaToBigInteger("id");
		if (null != id) {
			setAttr("content", Content.DAO.findById(id));
		}
		List<Content> contents = Content.DAO.findByModule("apiApp");
		setAttr("contents", contents);
		render("/WEB-INF/admin/option/api.html");
	}

	@ActionKey("/admin/api/save")
	public void save() {
		render("/WEB-INF/admin/option/api.html");
	}

}
