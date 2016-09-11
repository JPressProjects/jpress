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

import io.jpress.model.base.BaseMetadata;
import io.jpress.model.core.Table;

@Table(tableName = "metadata", primaryKey = "id")
public class Metadata extends BaseMetadata<Metadata> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean update() {
		removeCache(getId());
		removeCache(getObjectType() + getObjectId() + getMetaKey());
		return super.update();
	}

	@Override
	public boolean delete() {
		removeCache(getId());
		removeCache(getObjectType() + getObjectId() + getMetaKey());
		return super.delete();
	}

}
