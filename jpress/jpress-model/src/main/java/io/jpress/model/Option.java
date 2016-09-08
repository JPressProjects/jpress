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
package io.jpress.model;

import io.jpress.model.base.BaseOption;
import io.jpress.model.core.Table;

@Table(tableName = "option", primaryKey = "id")
public class Option extends BaseOption<Option> {

	private static final long serialVersionUID = 1L;

	public static final String KEY_WEB_NAME = "web_name";
	public static final String KEY_TEMPLATE_ID = "web_template_id";

	
	@Override
	public boolean update() {
		removeCache(getOptionKey());
		return super.update();
	}
	
	
	@Override
	public boolean save() {
		removeCache(getOptionKey());
		return super.save();
	}

	@Override
	public boolean delete() {
		removeCache(getOptionKey());
		return super.delete();
	}

}
