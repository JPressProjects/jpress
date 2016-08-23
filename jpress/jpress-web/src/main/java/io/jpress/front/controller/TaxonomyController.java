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
package io.jpress.front.controller;

import com.jfinal.render.Render;

import io.jpress.Consts;
import io.jpress.core.BaseFrontController;
import io.jpress.core.addon.HookInvoker;
import io.jpress.core.cache.ActionCache;
import io.jpress.model.Content;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.router.RouterMapping;
import io.jpress.template.TemplateUtils;
import io.jpress.ui.freemarker.tag.ContentPageTag;
import io.jpress.ui.freemarker.tag.MenuTag;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_TAXONOMY)
public class TaxonomyController extends BaseFrontController {

	private String module;
	private String slug;
	private Integer page;

	@ActionCache
	public void index() {
		try {
			Render render = onRenderBefore();
			if (render != null) {
				render(render);
			} else {
				doRender();
			}
		} finally {
			onRenderAfter();
		}
	}

	private void doRender() {
		initRequest();

		Taxonomy taxonomy = tryGetTaxonomy();
		if (slug != null && taxonomy == null) {
			renderError(404);
		}

		setAttr(Consts.ATTR_PAGE_NUMBER, page);
		setAttr("taxonomy", taxonomy);
		setAttr("module", TemplateUtils.currentTemplate().getModuleByName(module));

		if (taxonomy != null) {
			setGlobleAttrs(taxonomy);
			setAttr("jp_menu", new MenuTag(getRequest(), taxonomy));

			Content c = ContentQuery.me().findFirstByModuleAndObjectId(Consts.MODULE_MENU, taxonomy.getId());
			setAttr("jp_current_menu", c);
		}

		setAttr("contentPage", new ContentPageTag(page, module, taxonomy));

		if (null == taxonomy) {
			render(String.format("taxonomy_%s.html", module));
		} else {
			render(String.format("taxonomy_%s_%s.html", module, taxonomy.getSlug()));
		}
	}

	private void setGlobleAttrs(Taxonomy taxonomy) {
		setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, taxonomy.getTitle());
		setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, taxonomy.getMetaKeywords());
		setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, taxonomy.getMetaDescription());
	}

	private Taxonomy tryGetTaxonomy() {
		return slug == null ? null : TaxonomyQuery.me().findBySlugAndModule(slug, module);
	}

	private void initRequest() {
		module = getPara(0);
		if (module == null) {
			renderError(404);
		}

		if (TemplateUtils.currentTemplate().getModuleByName(module) == null) {
			renderError(404);
		}

		if (getParaCount() == 2) {
			String pageNumberOrSlug = getPara(1);
			if (StringUtils.isNumeric(pageNumberOrSlug)) {
				page = StringUtils.toInt(pageNumberOrSlug, 0);
			} else {
				slug = pageNumberOrSlug;
			}
		}
		// 3 para
		else if (getParaCount() >= 3) {
			slug = getPara(1);
			page = getParaToInt(2);
		}

		if (slug != null) {
			slug = StringUtils.urlDecode(slug);
		}

		if (page == null || page <= 0) {
			page = 1;
		}
	}

	private Render onRenderBefore() {
		return HookInvoker.taxonomyRenderBefore(this);
	}

	private void onRenderAfter() {
		HookInvoker.taxonomyRenderAfter(this);
	}

}
