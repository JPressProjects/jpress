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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.Consts;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Comment;
import io.jpress.model.Content;
import io.jpress.utils.StringUtils;

@UrlMapping(url = Consts.CONTENT_BASE_URL)
public class ContentController extends BaseFrontController {

	// http://www.xxx.com/c/123 content.id:123 page:1
	// http://www.xxx.com/c/123-2 content.id:123 page:2

	// http://www.xxx.com/c/abc content.slug:abc page:1
	// http://www.xxx.com/c/abc-2 content.slug:abc page:2

	public void index() {
		Content content = null;
		try {
			content = tryToGetContent();
		} catch (Exception e) {
		}
		if (null == content) {
			renderError(404);
			return;
		}

		int pageNumber = getParaToInt(1, 1);

		Page<Comment> page = Comment.DAO.doPaginateByContentId(pageNumber, 10, content.getId());

		setAttr("WEB_TITLE", content.getTitle());
		setAttr("META_KEYWORDS", content.getMetaKeywords());
		setAttr("META_DESCRIPTION", content.getMetaDescription());

		setAttr("pageNumber", pageNumber);
		setAttr("content", content);
		setAttr("page", page);
		setAttr("PAGE_URL",Consts.CONTENT_BASE_URL + "/" + content.getSlug() == null ? content.getId() : content.getSlug() + "-");
		render(String.format("content_%s_%s.html", content.getModule(), content.getStyle()));
	}

	private Content tryToGetContent() throws UnsupportedEncodingException {
		long id = StringUtils.toLong(getPara(0), (long) 0);
		return id > 0 ? Content.DAO.findById(id) : Content.DAO.findBySlug(URLDecoder.decode(getPara(0), "utf-8"));
	}

}
