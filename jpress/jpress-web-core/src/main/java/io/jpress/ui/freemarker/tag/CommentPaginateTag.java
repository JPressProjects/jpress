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

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.utils.StringUtils;

public class CommentPaginateTag extends BasePaginateTag {

	final Content content;

	public CommentPaginateTag(Page<Comment> page, Content content) {
		super(page);
		this.content = content;
	}

	@Override
	protected String getUrl(int pageNumber) {
		String url = content.getUrlWithPageNumber(pageNumber);
		if (StringUtils.isNotBlank(anchor)) {
			url += "#" + anchor;
		}
		return url;
	}


}
