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
package io.jpress.controller.front;

import java.math.BigInteger;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.Consts;
import io.jpress.core.cache.ActionCache;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.model.User;
import io.jpress.router.RouterMapping;
import io.jpress.ui.freemarker.tag.ContentPaginateTag;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_CONTENT)
public class ContentController extends BaseFrontController {

	private String slug;
	private BigInteger id;
	private int pageNumber;
	private int pageSize;

	@ActionCache
	public void index() {

		initRequest();

		Content content = queryContent();
		if (null == content) {
			renderError(404);
			return;
		}

		updateContentViewCount(content);
		
		setGlobleAttrs(content);

		setAttr("pageNumber", pageNumber);
		setAttr("content", content);
		setAttr("user", User.DAO.findById(content.getUserId()));

		Page<Comment> page = Comment.DAO.doPaginateByContentId(pageNumber, pageSize, content.getId());
		setAttr("page", page);
		
		ContentPaginateTag cpt = new ContentPaginateTag(page,content);
		setAttr("pagination", cpt);

		render(String.format("content_%s_%s.html", content.getModule(), content.getStyle()));
	}

	private void updateContentViewCount(Content content) {
		long visitorCount = VisitorCounter.getVisitorCount(content.getId());
		Long viewCount = content.getViewCount() == null ? visitorCount : content.getViewCount() + visitorCount;
		content.setViewCount(viewCount);
		if(content.update()){
			VisitorCounter.clearVisitorCount(content.getId());
		}
	}

	private void setGlobleAttrs(Content content) {
		setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, content.getTitle());
		setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, content.getMetaKeywords());
		setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, content.getMetaDescription());
	}

	private Content queryContent() {
		if (id != null) {
			return Content.DAO.findById(id);
		} else {
			return Content.DAO.findBySlug(StringUtils.urlDecode(slug));
		}
	}

	private void initRequest() {
		String idOrSlug = getPara(0);
		if (StringUtils.isNotBlank(idOrSlug)) {
			try {
				id = new BigInteger(idOrSlug);
			} catch (Exception e) {
				slug = idOrSlug;
			}
			pageNumber = getParaToInt(1, 1);
			pageSize = getParaToInt(2, 10);
		} else {
			id = getParaToBigInteger("id");
			slug = getPara("slug");
			pageNumber = getParaToInt("pageNumber", 1);
			pageSize = getParaToInt("pageSize", 10);
		}

	}

}
