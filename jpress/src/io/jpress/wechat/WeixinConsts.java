package io.jpress.wechat;

public class WeixinConsts {

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
	
	
}
