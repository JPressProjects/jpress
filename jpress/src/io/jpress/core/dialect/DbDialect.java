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
package io.jpress.core.dialect;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.plugin.druid.DruidPlugin;

public abstract class DbDialect {

	// key:classSimpleName.toLowerCase value:tableName
	protected static Map<String, String> tableMapping = new HashMap<String, String>();
	public static void mapping(String key, String value) {
		tableMapping.put(key, value);
	}

	public abstract String forShowTable();
	public abstract String forInstall(String tablePrefix);
	public abstract String forSelect(String tableName);
	public abstract String forDelete(String tableName);
	public abstract String forSelectCount(String tableName);
	public abstract String forPaginateFrom(String tableName,String where);
	public abstract String forInsertWebName(String tablePrefix);
	public abstract String forInsertFirstUser(String tablePrefix);
	
	/**
	 * becuse the table name is uncertainty,
	 * invoke this method convert sql to correct.
	 * @param sql
	 * @return
	 */
	public abstract String doTableConvert(String sql);

	public abstract DruidPlugin createDuidPlugin(String dbHost, String dbName,String dbUser, String dbPassword);

}
