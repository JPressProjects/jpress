/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 常量
 * @Package io.jpress
 */
public class JPressConsts {

    public static final String VERSION = "v2.0.7";

    /**
     * 后台系统菜单的 ID
     */
    public static final String SYSTEM_MENU_USER = "user";
    public static final String SYSTEM_MENU_ATTACHMENT = "attachment";
    public static final String SYSTEM_MENU_TEMPLATE = "template";
    public static final String SYSTEM_MENU_ADDON = "addon";
    public static final String SYSTEM_MENU_SYSTEM = "system";
    //    public static final String SYSTEM_MENU_APPEARANCE = "appearance";
//    public static final String SYSTEM_MENU_STATISTICS = "statistics";
    public static final String SYSTEM_MENU_FINANCE = "finance";
    public static final String SYSTEM_MENU_WECHAT_PUBULIC_ACCOUNT = "wechat_pubulic_account";
    public static final String SYSTEM_MENU_WECHAT_MINI_PROGRAM = "wechat_mini_program";


    /**
     * 以下是配置相关
     */
    public static final String OPTION_WEB_TITLE = "web_title"; //网站标题
    public static final String OPTION_WEB_SUBTITLE = "web_subtitle"; // 网站副标题
    public static final String OPTION_WEB_NAME = "web_name"; // 网站名称
    public static final String OPTION_WEB_DOMAIN = "web_domain"; // 网站域名
    public static final String OPTION_WEB_COPYRIGHT = "web_copyright"; // 网站版权信息
    public static final String OPTION_WEB_IPC_NO = "web_ipc_no"; // 网站备案号
    public static final String OPTION_SEO_TITLE = "seo_title"; // SEO 标题
    public static final String OPTION_SEO_KEYWORDS = "seo_keywords"; //  SEO 关键字
    public static final String OPTION_SEO_DESCRIPTION = "seo_description"; // SEO 描述


    public static final String OPTION_WEB_FAKE_STATIC_ENABLE = "web_fake_static_enable"; //是否启用伪静态
    public static final String OPTION_WEB_FAKE_STATIC_SUFFIX = "web_fake_static_suffix"; //网站伪静态后缀

    public static final String OPTION_CDN_ENABLE = "cdn_enable"; //是否启用CDN
    public static final String OPTION_CDN_DOMAIN = "cdn_domain"; //CDN域名

    public static final String OPTION_API_ENABLE = "api_enable"; //是否启用API
    public static final String OPTION_API_SECRET = "api_secret"; //API密钥
    public static final String OPTION_API_APPID = "api_app_id"; //API应用ID

    public static final String OPTION_WECHAT_WEB_AUTHORIZE_ENABLE = "wechat_web_authorize_enable"; //是否启用微信网页授权功能


    public static final String OPTION_WECHAT_APPID = "wechat_appid"; //微信的APP Id
    public static final String OPTION_WECHAT_APPSECRET = "wechat_appsecret"; //微信的 APP Secret
    public static final String OPTION_WECHAT_TOKEN = "wechat_token"; //微信的 token

    public static final String OPTION_WECHAT_MINIPROGRAM_APPID = "wechat_miniprogram_appid"; //微信小程序的 token
    public static final String OPTION_WECHAT_MINIPROGRAM_APPSECRET = "wechat_miniprogram_appsecret"; //微信小程序的 token
    public static final String OPTION_WECHAT_MINIPROGRAM_TOKEN = "wechat_miniprogram_token"; //微信小程序的 token


    public static final String OPTION_CONNECTION_EMAIL_ENABLE = "connection_email_enable"; // 是否启用邮件发送功能
    public static final String OPTION_CONNECTION_EMAIL_SMTP = "connection_email_smtp"; // 邮件服务器smtp
    public static final String OPTION_CONNECTION_EMAIL_ACCOUNT = "connection_email_account"; //邮箱账号
    public static final String OPTION_CONNECTION_EMAIL_PASSWORD = "connection_email_password"; //邮箱密码
    public static final String OPTION_CONNECTION_EMAIL_SSL_ENABLE = "connection_email_ssl_enable"; //是否启用ssl


    public static final String OPTION_CONNECTION_SMS_ENABLE = "connection_sms_enable"; //是否启用短信
    public static final String OPTION_CONNECTION_SMS_TYPE = "connection_sms_type"; //短信服务商
    public static final String OPTION_CONNECTION_SMS_APPID = "connection_sms_appid"; // 服务商 的appid（或者appKey）
    public static final String OPTION_CONNECTION_SMS_APPSECRET = "connection_sms_appsecret"; //app密钥


    public static final String SMS_TYPE_ALIYUN = "aliyun"; //短信服务商：阿里云
    public static final String SMS_TYPE_QCLOUD = "qcloud"; //短信服务商：腾讯云

    /**
     * 用到的cookie name 常量
     */
    public static final String COOKIE_UID = "_jpuid";
    public static final String COOKIE_ANONYM = "_jpanonym";
    public static final String COOKIE_EDIT_MODE = "_jpeditmode";

    /**
     * 用到的request attribute常量
     */
    public static final String ATTR_LOGINED_USER = "USER";


    public static final String ATTR_WEB_TITLE = "WEB_TITLE"; //网站标题
    public static final String ATTR_WEB_SUBTITLE = "WEB_SUBTITLE"; // 网站副标题
    public static final String ATTR_WEB_NAME = "WEB_NAME"; // 网站名称
    public static final String ATTR_WEB_DOMAIN = "WEB_DOMAIN"; // 网站域名
    public static final String ATTR_WEB_COPYRIGHT = "WEB_COPYRIGHT"; // 网站版权信息
    public static final String ATTR_WEB_IPC_NO = "WEB_IPC_NO"; // 网站备案号
    public static final String ATTR_SEO_TITLE = "SEO_TITLE"; // SEO 标题
    public static final String ATTR_SEO_KEYWORDS = "SEO_KEYWORDS"; //  SEO 关键字
    public static final String ATTR_SEO_DESCRIPTION = "SEO_DESCRIPTION"; // SEO 描述

    public static final String ATTR_MENUS = "MENUS"; // 页面菜单

    public static final String EDIT_MODE_HTML = "html"; //html 的编辑模式
    public static final String EDIT_MODE_MARKDOWN = "markdown"; //markdown 的编辑模式


    public static final String JWT_USERID = "userId";


    public static final String DEFAULT_ADMIN_VIEW = "/WEB-INF/views/admin/";
}
