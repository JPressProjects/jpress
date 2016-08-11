/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.wechat;

import java.util.HashMap;
import java.util.Map;

public class WechatConsts {

	public static final String TYPE_CLICK = "click"; // 点击推事件
	public static final String TYPE_VIEW = "view"; // 跳转URL
	public static final String TYPE_SCANCODE_PUSH = "scancode_push"; // 扫码推事件
	public static final String TYPE_SCANCODE_WAITMSG = "scancode_waitmsg"; // 扫码推事件且弹出“消息接收中”提示框
	public static final String TYPE_PIC_SYSPHOTO = "pic_sysphoto"; // 弹出系统拍照发图
	public static final String TYPE_PIC_PHOTO_OR_ALBUM = "pic_photo_or_album"; // 弹出拍照或者相册发图
	public static final String TYPE_PIC_WEIXIN = "pic_weixin"; // 弹出微信相册发图器
	public static final String TYPE_LOCATION_SELECT = "location_select"; // 弹出地理位置选择器

	// 以下 TYPE_MEDIA_ID 和 TYPE_VIEW_LIMITED 是专门给第三方平台旗下未微信认证
	public static final String TYPE_MEDIA_ID = "media_id"; // 下发消息（除文本消息）
	public static final String TYPE_VIEW_LIMITED = "view_limited"; // 跳转图文消息URL
	
	
	static Map<Integer, String> errors = new HashMap<Integer, String>();

	static {
		errors.put(-1, "微信服务器繁忙，此时请稍候再试");
		errors.put(40001, "获取accessToken时AppSecret错误，或者accessToken无效");
		errors.put(40013, "不合法的AppID，请开发者检查AppID的正确性，避免异常字符，注意大小写");
		errors.put(40015, "不合法的菜单类型");
		errors.put(40016, "菜单按钮数量超出限制,一级菜单最多3个,二级菜单最多5个");
		errors.put(40017, "菜单按钮数量超出限制");
		errors.put(40018, "一级菜单名称长度超出限制,一级菜单最多4个汉字");
		errors.put(40019, "菜单点击事件的key值长度超出限制");
		errors.put(40020, "菜单URL长度超出限制");
		errors.put(40021, "不合法的菜单版本号");
		errors.put(40022, "菜单级数超出限制,请确保只有两级菜单,且一级菜单不超过三个,二级菜单不超过5个");
		errors.put(40023, "二级菜单数量超出限制,请确保每个一级菜单下的二级菜单不超过5个");
		errors.put(40024, "菜单类型错误,请检查菜单类型是否是key和URL的其中一个");
		errors.put(40025, "二级菜单名称长度超出限制,二级菜单最多7个汉字");
		errors.put(40026, "二级菜单点击事件的key值长度超出限制");
		errors.put(40027, "二级菜单URL长度超出限制");
		errors.put(40119, "请确保你的公众号非个人号且已获得认证");
		errors.put(40120, "请确保你的公众号非个人号且已获得认证");
		errors.put(48001, "您的公众号还未获得该API授权。");
	}

	public static String getErrorMessage(int errorCode) {
		return errors.get(errorCode);
	}
}
