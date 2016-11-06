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
package io.jpress.model.core;

import java.util.HashMap;
import java.util.Map;

public class JModelMapping {

	protected static Map<String, String> tableMapping = new HashMap<String, String>();

	public void mapping(String key, String value) {
		tableMapping.put(key, value);
	}

	private static JModelMapping me = new JModelMapping();

	public static JModelMapping me() {
		return me;
	}

	private JModelMapping() {
	}

	public String tx(String sql) {
		for (Map.Entry<String, String> entry : tableMapping.entrySet()) {
			sql = sql.replace(" " + entry.getKey() + " ", String.format(" `%s` ", entry.getValue()));
			sql = sql.replace(" " + entry.getKey() + ",", String.format(" `%s`,", entry.getValue()));
			sql = sql.replace(" " + entry.getKey() + ".", String.format(" `%s`.", entry.getValue()));
			sql = sql.replace("," + entry.getKey() + " ", String.format(",`%s` ", entry.getValue()));
			sql = sql.replace("," + entry.getKey() + ".", String.format(",`%s`.", entry.getValue()));
			// sql = sql.replace(entry.getKey() + "`", entry.getValue() + "`");
		}
		return sql;
	}

}
