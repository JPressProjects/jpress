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

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.Attachment;

public class AttachmentQuery extends JBaseQuery {

	private static final Attachment DAO = new Attachment();
	private static final AttachmentQuery QUERY = new AttachmentQuery();
	
	public static AttachmentQuery me(){
		return QUERY;
	}

	public Page<Attachment> paginate(int pageNumber, int pageSize) {
		String sqlExceptSelect = " FROM attachment a ORDER BY a.created DESC";
		return DAO.paginate(pageNumber, pageSize, "SELECT * ", sqlExceptSelect);
	}
	
	public Attachment findById(BigInteger id){
		return DAO.findById(id);
	}

}
