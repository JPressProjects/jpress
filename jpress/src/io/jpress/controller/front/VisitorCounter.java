package io.jpress.controller.front;

import java.math.BigInteger;

import com.jfinal.aop.Clear;
import com.jfinal.plugin.ehcache.CacheKit;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.router.RouterNotAllowConvert;


@Clear
@RouterNotAllowConvert
@UrlMapping(url = "/counter")
public class VisitorCounter extends JBaseController {

	private static final String CACHE_NAME = "visitor_counter";

	public void index() {
		BigInteger id = getParaToBigInteger("cid");
		if (id == null) {
			renderJavascript("");
			return;
		}

		Long visitorCount = CacheKit.get(CACHE_NAME, "cid:" + id);
		visitorCount = visitorCount == null ? 0 : visitorCount;
		CacheKit.put(CACHE_NAME, "cid:" + id, visitorCount + 1);
		renderJavascript("");
	}

	public void show() {
		BigInteger id = getParaToBigInteger("cid");
		if (id == null) {
			renderNull();
			return;
		}

		Long visitorCount = CacheKit.get(CACHE_NAME, "cid:" + id);
		visitorCount = visitorCount == null ? 0 : visitorCount;
		renderText(visitorCount + "");
	}

	public static long getVisitorCount(BigInteger id) {
		Long visitorCount = CacheKit.get(CACHE_NAME, "cid:" + id);
		visitorCount = visitorCount == null ? 0 : visitorCount;
		return visitorCount;
	}

}
