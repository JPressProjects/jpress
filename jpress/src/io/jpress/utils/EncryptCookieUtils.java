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
package io.jpress.utils;

import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;

/**
 * 参考：spring-security
 * https://github.com/spring-projects/spring-security/
 * blob/master/web/src/main/java/org/springframework/security/
 * web/authentication/rememberme/TokenBasedRememberMeServices.java
 * ....AbstractRememberMeServices.java
 */
public class EncryptCookieUtils {

	private final static String COOKIE_SEPARATOR = "#JP#";
	
	public static void put(Controller ctr, String key, String value) {
		put(ctr, key, value, 60 * 60 * 24 * 7);
	}
	
	public static void put(Controller ctr, String key, long value) {
		put(ctr, key, value+"");
	}
	
	public static void put(Controller ctr, String key, String value,int maxAgeInSeconds) {
		String cookie_encrypt_key = PropKit.get("encrypt_key");
		String cookieValue = value + COOKIE_SEPARATOR + HashKit.md5(cookie_encrypt_key + value);
		ctr.setCookie(key, cookieValue, maxAgeInSeconds);
		
	}

	public static void remove(Controller ctr, String key) {
		ctr.removeCookie(key);
	}

	public static String get(Controller ctr, String key) {
		
		String cookie_encrypt_key = PropKit.get("encrypt_key");
		String cookieValue = ctr.getCookie(key);
		
		if (null != cookieValue) {
			String userinfos[] = cookieValue.split(COOKIE_SEPARATOR);
			if (null != userinfos && 2 == userinfos.length) {
				// 保证 cookie 不被人为修改
				if (userinfos[1].equals(HashKit.md5(cookie_encrypt_key + userinfos[0]))) {
					return userinfos[0];
				}
			}
		}

		return null;
	}

}
