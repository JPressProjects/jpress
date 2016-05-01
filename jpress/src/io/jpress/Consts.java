/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress;

public class Consts {

	public static final String COOKIE_LOGINED_USER = "user";

	public static final String SYS_MODULE_PAGE = "page";

	public static final String CONTENT_BASE_URL = "/c";
	public static final String TAXONOMY_BASE_URL = "/t";
	public static final String USER_BASE_URL = "/user";
	public static final String USER_CENTER_BASE_URL = USER_BASE_URL + "/center";
	public static final String LOGIN_BASE_URL = USER_BASE_URL + "/login";

	public static final int ERROR_CODE_NOT_VALIDATE_CAPTHCHE = 1;
	public static final int ERROR_CODE_USERNAME_EMPTY = 2;
	public static final int ERROR_CODE_USERNAME_EXIST = 3;
	public static final int ERROR_CODE_EMAIL_EMPTY = 4;
	public static final int ERROR_CODE_EMAIL_EXIST = 5;
	public static final int ERROR_CODE_PHONE_EMPTY = 6;
	public static final int ERROR_CODE_PHONE_EXIST = 7;
	public static final int ERROR_CODE_PASSWORD_EMPTY = 8;

	public static final String ATTR_PAGE_NUMBER = "_page_number";
	public static final String ATTR_MUDULE = "_mudule";
	public static final String ATTR_USER = "user";
	
	
	public static final String MODULE_ARTICLE = "article";
	public static final String MODULE_PAGE = "page";
	public static final String MODULE_FOURM = "forum";
	public static final String MODULE_MENU = "menu";
	public static final String MODULE_QA = "qa";
	public static final String MODULE_GOODS = "goods";
	public static final String MODULE_GOODS_ORDER = "goods_order";
	public static final String MODULE_WECHAT_MENU = "wechat_menu";
	public static final String MODULE_WECHAT_REPLY = "wechat_reply";
	public static final String MODULE_USER_COLLECTION = "user_collection";
}
