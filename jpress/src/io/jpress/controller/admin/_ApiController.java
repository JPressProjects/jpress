package io.jpress.controller.admin;

import java.math.BigInteger;
import java.util.List;

import com.jfinal.aop.Before;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.utils.StringUtils;

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

		Boolean apiEnable = getParaToBoolean("api_enable", Boolean.FALSE);
		Option.saveOrUpdate("api_enable", apiEnable.toString());

		Content c = getModel(Content.class);
		
		if(StringUtils.areNotBlank(c.getTitle(),c.getText(),c.getFlag())){
			c.saveOrUpdate();
		}
		
		renderAjaxResultForSuccess();
	}

}
