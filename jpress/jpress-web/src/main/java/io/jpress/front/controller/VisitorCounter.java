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
package io.jpress.front.controller;

import java.math.BigInteger;

import com.jfinal.aop.Clear;
import com.jfinal.plugin.ehcache.CacheKit;

import io.jpress.core.JBaseController;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;

@Clear
@RouterNotAllowConvert
@RouterMapping(url = "/counter")
public class VisitorCounter extends JBaseController {

	private static final String CACHE_NAME = "visitor_counter";
	private static final String CID = "cid_";

	public void index() {
		BigInteger id = getParaToBigInteger("cid");
		if (id == null) {
			renderJavascript("");
			return;
		}

		Long visitorCount = CacheKit.get(CACHE_NAME, buildKey(id));
		visitorCount = visitorCount == null ? 0 : visitorCount;
		CacheKit.put(CACHE_NAME, buildKey(id), visitorCount + 1);
		renderJavascript("");
	}

	public void show() {
		BigInteger id = getParaToBigInteger("cid");
		if (id == null) {
			renderNull();
			return;
		}

		Long visitorCount = CacheKit.get(CACHE_NAME, buildKey(id));
		visitorCount = visitorCount == null ? 0 : visitorCount;
		renderText(visitorCount + "");
	}

	public static long getVisitorCount(BigInteger id) {
		Long visitorCount = CacheKit.get(CACHE_NAME, buildKey(id));
		return visitorCount == null ? 0 : visitorCount;
	}

	public static void clearVisitorCount(BigInteger id) {
		CacheKit.remove(CACHE_NAME, buildKey(id));
	}

	private static String buildKey(BigInteger id) {
		return CID + id;
	}

}
