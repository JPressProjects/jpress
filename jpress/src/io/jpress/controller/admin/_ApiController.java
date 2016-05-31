package io.jpress.controller.admin;

import java.math.BigInteger;
import java.util.List;

import com.jfinal.aop.Before;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.Content;

@UrlMapping(url = "/admin/api")
@Before(ActionCacheClearInterceptor.class)
public class _ApiController extends JBaseController {

	public void index() {
		BigInteger id = getParaToBigInteger("id");
		if (null != id) {
			setAttr("content", Content.DAO.findById(id));
		}
		List<Content> contents = Content.DAO.findByModule("apiApplication");
		setAttr("contents", contents);
		render("/WEB-INF/admin/option/api.html");
	}

	public void save() {
		Content c = getModel(Content.class);
		if (c != null) {
			c.saveOrUpdate();
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

}
