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
package io.jpress.ui.freemarker.tag;

import java.math.BigInteger;

import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;

public class UserContentPageTag extends JTag {

	BigInteger userId;
	int pageNumber;

	public UserContentPageTag(BigInteger id, int pageNumber) {
		this.userId = id;
		this.pageNumber = pageNumber;
	}

	@Override
	public void onRender() {

		String module = getParam("module");
		BigInteger taxonomyId = getParamToBigInteger("taxonomyId");

		int pageSize = getParamToInt("pagesize", 10);
		String orderby = getParam("orderby");
		String status = getParam("status", Content.STATUS_NORMAL);

		setVariable("page", ContentQuery.me().paginate(pageNumber, pageSize, module, null, status,
				new BigInteger[] { taxonomyId }, null, orderby));

		renderBody();
	}

}
