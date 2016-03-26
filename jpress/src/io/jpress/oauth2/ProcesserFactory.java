package io.jpress.oauth2;

import io.jpress.oauth2.processer.OSChinaProcesser;
import io.jpress.oauth2.processer.QQProcesser;
import io.jpress.oauth2.processer.WechatProcesser;
import io.jpress.oauth2.processer.WeiboProcesser;

import java.util.HashMap;
import java.util.Map;

public class ProcesserFactory {

	private static final Map<String, OauthProcesser> oauths = new HashMap<String, OauthProcesser>();
	static {
		oauths.put("qq", new QQProcesser());
		oauths.put("oschina", new OSChinaProcesser());
		oauths.put("wechat", new WechatProcesser());
		oauths.put("weibo", new WeiboProcesser());
	}

	public static OauthProcesser createProcesser(String key) {
		return oauths.get(key);
	}

}
