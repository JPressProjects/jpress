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
import java.sql.SQLException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import io.jpress.core.db.Jdb;
import io.jpress.model.Mapping;

public class MappingQuery extends JBaseQuery {

	private static final Mapping MODEL = new Mapping();

	public static int doDelByContentId(BigInteger contentId) {
		return MODEL.doDelete("content_id = ?", contentId);
	}

	public static boolean doBatchUpdate(final BigInteger contentId, final BigInteger[] taxonomyIds) {
		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				doDelByContentId(contentId);
				for (BigInteger taxonomyid : taxonomyIds) {
					Mapping mapping = new Mapping();
					mapping.setContentId(contentId);
					mapping.setTaxonomyId(taxonomyid);
					if(!mapping.save()){
						return false;
					}
				}
				return true;
			}
		});
	}
	
	
	public static void deleteByContentId(BigInteger id){
		Jdb.update("DELETE FROM mapping WHERE content_id = ?",id);
	}
	
	public static void  deleteByTaxonomyId(BigInteger id){
		Jdb.update("DELETE FROM mapping WHERE taxonomy_id = ? ",id) ;
	}

	public static long findCountByTaxonomyId(BigInteger id) {
		return MODEL.doFindCount("taxonomy_id = ?", id);
	}
	
	
	public static long findCountByTaxonomyId(BigInteger id,String contentStatus) {
		String sql = "SELECT COUNT(*) FROM mapping m ";
		sql += "left join content c ON m.content_id=c.id ";
		sql += "where m.taxonomy_id = ? and c.status = ?";
		return Jdb.queryLong(sql,id,contentStatus);
	}

	public static long findCountByContentId(BigInteger id) {
		return MODEL.doFindCount("content_id = ?", id);
	}
}
