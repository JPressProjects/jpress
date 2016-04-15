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
package io.jpress.core;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.token.ITokenCache;
import com.jfinal.token.Token;

public class JTokenCache implements ITokenCache {

	@Override
	public void put(Token token) {
		CacheKit.put("token", token.getId(), token);
	}

	@Override
	public void remove(Token token) {
		CacheKit.remove("token", token.getId());
	}

	@Override
	public boolean contains(Token token) {
		@SuppressWarnings("unchecked")
		List<String> keys = CacheKit.getKeys("token");
		return keys == null ? false : keys.contains(token.getId());
	}

	@Override
	public List<Token> getAll() {
		List<Token> ret = new ArrayList<Token>();
		try {
			@SuppressWarnings("unchecked")
			List<String> keys = CacheKit.getKeys("token");
			if (null != keys && keys.size() > 0) {
				for (String key : keys) {
					ret.add((Token) CacheKit.get("token", key));
				}
			}
		} catch (Exception e) {
			// jfinal 2.2 has a bug in tokenManager
			// java.lang.IllegalStateException: The CacheManager has been shut
			// down. It can no longer be used.
			// e.printStackTrace();
		}
		return ret;
	}

}
