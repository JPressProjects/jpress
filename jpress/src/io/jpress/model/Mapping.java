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

import java.sql.SQLException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseMapping;

@Table(tableName="mapping",primaryKey="id")
public class Mapping extends BaseMapping<Mapping> {

	private static final long serialVersionUID = 1L;
	
	public static final Mapping DAO = new Mapping();
	
	
	public int doDelByContentId(long contentId){
		return doDelete("content_id = ?", contentId);
	}
	
	
	public boolean doBatchUpdate(final long contentId,final Long[] taxonomyIds){
		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				doDelByContentId(contentId);
				for(long taxonomyid : taxonomyIds){
					Mapping mapping = new Mapping();
					mapping.setContentId(contentId);
					mapping.setTaxonomyId(taxonomyid);
					mapping.save();
				}
				return true;
			}
		});
	}

}
