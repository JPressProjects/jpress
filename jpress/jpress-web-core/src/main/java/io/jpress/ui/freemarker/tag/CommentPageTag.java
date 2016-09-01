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

import javax.servlet.http.HttpServletRequest;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.render.freemarker.BasePaginateTag;
import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.query.CommentQuery;
import io.jpress.utils.StringUtils;

public class CommentPageTag extends JTag {
	
	public static final String TAG_NAME = "jp.commentPage";

	Content content;
	int pageNumber;
	HttpServletRequest request;

	public CommentPageTag(HttpServletRequest request, Content content, int pageNumber) {
		this.request = request;
		this.content = content;
		this.pageNumber = pageNumber;
	}

	@Override
	public void onRender() {

		int pageSize = getParamToInt("pageSize", 10);

		Page<Comment> page = CommentQuery.me().paginateByContentId(pageNumber, pageSize, content.getId());
		setVariable("page", page);
		setVariable("comments", page.getList());
		
		CommentPaginateTag pagination = new CommentPaginateTag(request, page, content);
		setVariable("pagination", pagination);

		renderBody();
	}

	public static class CommentPaginateTag extends BasePaginateTag {

		final Content content;
		final HttpServletRequest request;

		public CommentPaginateTag(HttpServletRequest request, Page<Comment> page, Content content) {
			super(page);
			this.request = request;
			this.content = content;
		}

		@Override
		protected String getUrl(int pageNumber) {
			String url = content.getUrlWithPageNumber(pageNumber);

			String queryString = request.getQueryString();
			if (StringUtils.isNotBlank(queryString)) {
				url += "?" + queryString;
			}

			if (StringUtils.isNotBlank(getAnchor())) {
				url += "#" + getAnchor();
			}
			return JFinal.me().getContextPath() + url;
		}

	}

}
