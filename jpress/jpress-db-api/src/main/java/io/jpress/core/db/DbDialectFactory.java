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
package io.jpress.core.db;

import java.util.List;

import com.jfinal.log.Log;

import io.jpress.utils.ClassUtils;

public class DbDialectFactory {

	static final Log log = Log.getLog(DbDialectFactory.class);

	static DbDialect dialect;

	public static DbDialect getDbDialect() {
		return dialect;
	}

	private static void initDialect(Class<? extends DbDialect> clazz) {
		try {
			dialect = (DbDialect) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		List<Class<DbDialect>> list = ClassUtils.scanSubClass(DbDialect.class, true);

		if (list == null || list.isEmpty()) {
			throw new RuntimeException("can't scan DbDialect implement class in class path.");
		}

		if (list.size() > 1) {
			log.warn("there are too many DbDialect");
		}

		initDialect(list.get(0));
	}

}
