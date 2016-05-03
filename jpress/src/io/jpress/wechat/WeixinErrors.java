package io.jpress.wechat;

import java.util.HashMap;
import java.util.Map;

public class WeixinErrors {

	static Map<Integer, String> errors = new HashMap<Integer, String>();

	static {
		errors.put(40015, "不合法的菜单类型");
		errors.put(40016, "菜单按钮数量超出限制,一级菜单最多3个,二级菜单最多5个");
		errors.put(40017, "菜单按钮数量超出限制");
		errors.put(40018, "一级菜单名称长度超出限制,一级菜单最多4个汉字");
		errors.put(40019, "菜单点击事件的key值长度超出限制");
		errors.put(40020, "菜单URL长度超出限制");
		errors.put(40021, "");
		errors.put(40022, "菜单级数超出限制,请确保只有两级菜单,且一级菜单不超过三个,二级菜单不超过5个");
		errors.put(40023, "二级菜单数量超出限制,请确保每个一级菜单下的二级菜单不超过5个");
		errors.put(40024, "菜单类型错误,请检查菜单类型是否是key和URL的其中一个");
		errors.put(40025, "二级菜单名称长度超出限制,二级菜单最多7个汉字");
		errors.put(40026, "二级菜单点击事件的key值长度超出限制");
		errors.put(40027, "二级菜单URL长度超出限制");
		errors.put(40119, "请确保你的公众号非个人号且已获得认证");
		errors.put(40120, "请确保你的公众号非个人号且已获得认证");
	}

	public static String getMessage(int errorCode) {
		return errors.get(errorCode);
	}

}
