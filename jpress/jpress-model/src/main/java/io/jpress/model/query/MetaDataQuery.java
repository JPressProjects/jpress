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

import java.math.BigInteger;
import java.util.List;

import io.jpress.model.Metadata;

public class MetaDataQuery extends JBaseQuery {

	protected static final Metadata DAO = new Metadata();
	private static final MetaDataQuery QUERY = new MetaDataQuery();

	public static MetaDataQuery me() {
		return QUERY;
	}

	public List<Metadata> findListByTypeAndId(String type, BigInteger id) {
		return DAO.doFind("object_type = ? and object_id = ?", type, id);
	}

	public Metadata findFirstByTypeAndValue(String type, String key, Object value) {

		return DAO.doFindFirst("object_type = ? and meta_key = ? and meta_value = ?", type, key, value);

	}

	public List<Metadata> findListByTypeAndValue(String type, String key, Object value) {

		return DAO.doFind("object_type = ? and meta_key = ? and meta_value = ?", type, key, value);

	}

	public Metadata findByTypeAndIdAndKey(String type, BigInteger id, String key) {
		String cachekey = type + id + key;
		return DAO.doFindFirstByCache(Metadata.CACHE_NAME, cachekey,
				"object_type = ? and object_id = ? and meta_key = ? ", type, id, key);

	}
}
