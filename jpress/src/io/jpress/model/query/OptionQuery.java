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
package io.jpress.model.query;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.model.Option;
import io.jpress.utils.StringUtils;

public class OptionQuery extends JBaseQuery {

	private static Option MODEL = new Option();


	public static String findValue(final String key) {
		String value = CacheKit.get(Option.CACHE_NAME, key, new IDataLoader() {
			@Override
			public Object load() {
				Option option = MODEL.doFindFirst("option_key =  ?", key);
				if (null != option && option.getOptionValue() != null) {
					return option.getOptionValue();
				}
				return "";
			}
		});
		
		return "".equals(value) ? null : value;
	}
	
	public static void saveOrUpdate(String key, String value) {
		Option option = MODEL.doFindFirst("option_key =  ?", key);
		if (null == option) {
			option = new Option();
		}

		option.setOptionKey(key);
		option.setOptionValue(value);

		option.saveOrUpdate();
	}

	public static Option findByKey(String key) {
		return MODEL.doFindFirst("option_key =  ?", key);
	}

	public static Boolean findValueAsBool(String key) {
		String value = findValue(key);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Integer findValueAsInteger(String key) {
		String value = findValue(key);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static Float findValueAsFloat(String key) {
		String value = findValue(key);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Float.parseFloat(value);
			} catch (Exception e) {
			}
		}
		return null;
	}
}
