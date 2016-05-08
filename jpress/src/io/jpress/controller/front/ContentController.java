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
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.router.RouterParams;
import io.jpress.utils.StringUtils;

@UrlMapping(url = Consts.ROUTER_CONTENT)
public class ContentController extends BaseFrontController {

	public void index() {

		BigInteger id = null;
		String slug = null;
		int pageNumber = 0;
		int pageSize = 0;

		RouterParams _paraMap = getAttr(Consts.ATTR_ROUTER_ATTRS_MAP);

		if (_paraMap != null && _paraMap.size() > 0) {
			id = _paraMap.id();
			slug = _paraMap.slug();
			pageNumber = _paraMap.pageNumberWithDefault(1);
			pageSize = _paraMap.pageSizeWithDefault(10);
		} else {
			id = getParaToBigInteger("id");
			slug = getPara("slug");
			pageNumber = getParaToInt("pageNumber", 1);
			pageSize = getParaToInt("pageSize", 10);
		}

		if (id == null && slug == null) {
			renderError(404);
			return;
		}

		Content content = id != null ? Content.DAO.findById(id) : Content.DAO.findBySlug(StringUtils.urlDecode(slug));
		if (null == content) {
			renderError(404);
			return;
		}

		Page<Comment> page = Comment.DAO.doPaginateByContentId(pageNumber, pageSize, content.getId());

		setAttr("WEB_TITLE", content.getTitle());
		setAttr("META_KEYWORDS", content.getMetaKeywords());
		setAttr("META_DESCRIPTION", content.getMetaDescription());

		setAttr("pageNumber", pageNumber);
		setAttr("content", content);
		setAttr("page", page);
		setAttr("PAGE_URL",
				Consts.ROUTER_CONTENT + "/" + content.getSlug() == null ? content.getId() : content.getSlug() + "-");
		render(String.format("content_%s_%s.html", content.getModule(), content.getStyle()));
	}

}
