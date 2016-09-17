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

import java.util.List;

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
import io.jpress.template.TemplateManager;
import io.jpress.template.TplModule;
import io.jpress.ui.freemarker.tag.ContentPageTag;
import io.jpress.ui.freemarker.tag.MenusTag;
import io.jpress.ui.freemarker.tag.TaxonomysTag;
import io.jpress.utils.StringUtils;

@RouterMapping(url = Consts.ROUTER_TAXONOMY)
public class TaxonomyController extends BaseFrontController {

	protected TplModule module;
	protected String[] slugs;
	protected Integer page;

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

		List<Taxonomy> taxonomys = tryGetTaxonomy();
		if (slugs != null && slugs.length > 0 && taxonomys == null) {
			renderError(404);
		}

		Taxonomy taxonomy = null;
		if (taxonomys != null && !taxonomys.isEmpty()) {
			taxonomy = taxonomys.get(0);
		}

		setAttr(Consts.ATTR_PAGE_NUMBER, page);
		setAttr("taxonomys", taxonomys);
		setAttr("taxonomy", taxonomy);
		setAttr("module", module);

		setGlobleAttrs(taxonomys);
		setAttr(MenusTag.TAG_NAME, new MenusTag(getRequest(), taxonomys, null));
		setAttr(TaxonomysTag.TAG_NAME, new TaxonomysTag(taxonomys));

		if (taxonomy != null) {
			Content c = ContentQuery.me().findFirstByModuleAndObjectId(Consts.MODULE_MENU, taxonomy.getId());
			setAttr("currentMenu", c);
		}
		
		String order = getPara("order"); 
		if(module.isSupportOrder(order)){
			setAttr(ContentPageTag.TAG_NAME, new ContentPageTag(getRequest(), page, module.getName(), taxonomys, order));
		}else{
			setAttr(ContentPageTag.TAG_NAME, new ContentPageTag(getRequest(), page, module.getName(), taxonomys, null));
		}

		
		if (taxonomys == null || taxonomys.size() != 1) {
			render(String.format("taxonomy_%s.html", module.getName()));
		} else {
			render(String.format("taxonomy_%s_%s.html", module.getName(), taxonomys.get(0).getSlug()));
		}
	}

	private void setGlobleAttrs(List<Taxonomy> taxonomys) {
		if (taxonomys == null || taxonomys.isEmpty()) {
			return;
		}

		StringBuffer title = new StringBuffer();
		StringBuffer keywords = new StringBuffer();
		StringBuffer description = new StringBuffer();

		for (Taxonomy taxonomy : taxonomys) {
			title.append(taxonomy.getTitle()).append("-");
			keywords.append(taxonomy.getMetaKeywords()).append(",");
			description.append(taxonomy.getMetaDescription()).append(";");
		}

		title.deleteCharAt(title.length() - 1);
		keywords.deleteCharAt(keywords.length() - 1);
		description.deleteCharAt(description.length() - 1);

		setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, title.toString());
		setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, keywords.toString());
		setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, description.toString());
	}

	private List<Taxonomy> tryGetTaxonomy() {
		return slugs == null || slugs.length == 0 ? null : TaxonomyQuery.me().findBySlugAndModule(slugs, module.getName());
	}

	/**
	 * 分类的url种类： 种类1： http://www.xxx.com/module 该module下的所有内容 种类2：
	 * http://www.xxx.com/module-slug 该module下的slug分类的所有内容 种类3：
	 * http://www.xxx.com/module-slug1-slug2-slug3
	 * 该module下的、slug1、slug2、slug3、slug4的所有内容
	 */
	private void initRequest() {
		String moduleName = getPara(0);
		if (StringUtils.isBlank(moduleName)) {
			renderError(404);
		}

		module = TemplateManager.me().currentTemplateModule(moduleName);

		if (module == null) {
			renderError(404);
		}

		int paraCount = getParaCount();
		if (paraCount == 1) {
			page = 1;
		}

		String slugStrings = null;
		if (paraCount >= 2) {
			for (int i = 1; i < paraCount; i++) {
				String para = getPara(i);
				if (StringUtils.isNumeric(para)) {
					page = StringUtils.toInt(para, 1);
					if (i != paraCount - 1) {
						renderError(404);
					}
				} else {
					slugStrings = StringUtils.urlDecode(para);
				}
			}
		}

		if (StringUtils.isNotBlank(slugStrings)) {
			slugs = slugStrings.split(",");
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
