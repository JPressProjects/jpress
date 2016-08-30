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

import java.math.BigInteger;

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
public class CookieUtils {

	private final static String COOKIE_SEPARATOR = "#JP#";

	public static void put(Controller ctr, String key, String value) {
		put(ctr, key, value, 60 * 60 * 24 * 7);
	}

	public static void put(Controller ctr, String key, BigInteger value) {
		put(ctr, key, value.toString());
	}
	
	public static void put(Controller ctr, String key, long value) {
		put(ctr, key, value+"");
	}

	public static void put(Controller ctr, String key, String value, int maxAgeInSeconds) {
		String encrypt_key = PropKit.get("encrypt_key");
		String saveTime = System.currentTimeMillis() + "";
		String encrypt_value = encrypt(encrypt_key, saveTime, maxAgeInSeconds + "", value);

		String cookieValue = encrypt_value + COOKIE_SEPARATOR + saveTime + COOKIE_SEPARATOR + maxAgeInSeconds
				+ COOKIE_SEPARATOR + value;

		ctr.setCookie(key, cookieValue, maxAgeInSeconds);

	}

	private static String encrypt(String encrypt_key, String saveTime, String maxAgeInSeconds, String value) {
		return HashKit.md5(encrypt_key + saveTime + maxAgeInSeconds + value);
	}

	public static void remove(Controller ctr, String key) {
		ctr.removeCookie(key);
	}

	public static String get(Controller ctr, String key) {

		String encrypt_key = PropKit.get("encrypt_key");
		String cookieValue = ctr.getCookie(key);

		return getFromCookieInfo(encrypt_key, cookieValue);
	}

	public static String getFromCookieInfo(String encrypt_key, String cookieValue) {
		if (StringUtils.isNotBlank(cookieValue)) {
			String cookieStrings[] = cookieValue.split(COOKIE_SEPARATOR);
			if (null != cookieStrings && 4 == cookieStrings.length) {
				String encrypt_value = cookieStrings[0];
				String saveTime = cookieStrings[1];
				String maxAgeInSeconds = cookieStrings[2];
				String value = cookieStrings[3];

				String encrypt = encrypt(encrypt_key, saveTime, maxAgeInSeconds, value);

				// 保证 cookie 不被人为修改
				if (encrypt_value != null && encrypt_value.equals(encrypt)) {
					long stime = Long.parseLong(saveTime);
					long maxtime = Long.parseLong(maxAgeInSeconds) * 1000;
					// 查看是否过时
					if ((stime + maxtime) - System.currentTimeMillis() > 0) {
						return value;
					}
				}
			}
		}
		return null;
	}

	public static Long getLong(Controller ctr, String key) {
		String value = get(ctr, key);
		return null == value ? null : Long.parseLong(value);
	}

	public static long getLong(Controller ctr, String key, long defalut) {
		String value = get(ctr, key);
		return null == value ? defalut : Long.parseLong(value);
	}

	public static Integer getInt(Controller ctr, String key) {
		String value = get(ctr, key);
		return null == value ? null : Integer.parseInt(value);
	}

	public static int getLong(Controller ctr, String key, int defalut) {
		String value = get(ctr, key);
		return null == value ? defalut : Integer.parseInt(value);
	}

}
