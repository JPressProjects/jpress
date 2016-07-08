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

import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;

public class ContentPageTag extends JTag {
	int pageNumber;
	String moduleName;
	Taxonomy taxonomy;

	public ContentPageTag(int pageNumber, String moduleName, Taxonomy taxonomy) {
		this.pageNumber = pageNumber;
		this.moduleName = moduleName;
		this.taxonomy = taxonomy;
	}

	@Override
	public void onRender() {

		int pagesize = getParamToInt("pagesize", 10);

		BigInteger[] tids = taxonomy == null ? null : new BigInteger[] { taxonomy.getId() };

		Page<Content> page = ContentQuery.paginate(pageNumber, pagesize, moduleName, null, Content.STATUS_NORMAL, tids,
				null, null);
		setVariable("page", page);

		ContentPaginateTag tpt = new ContentPaginateTag(page, moduleName, taxonomy);
		setVariable("pagination", tpt);

		renderBody();
	}

}
