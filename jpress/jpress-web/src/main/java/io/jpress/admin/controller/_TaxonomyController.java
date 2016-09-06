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
package io.jpress.admin.controller;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.Consts;
import io.jpress.core.JBaseCRUDController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.MappingQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.TemplateManager;
import io.jpress.template.TplModule;
import io.jpress.template.TplTaxonomyType;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/admin/taxonomy", viewPath = "/WEB-INF/admin/taxonomy")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _TaxonomyController extends JBaseCRUDController<Taxonomy> {

	private String getContentModule() {
		return getPara("m");
	}

	private String getType() {
		return getPara("t");
	}

	public void index() {
		String moduleName = getContentModule();
		TplModule module = TemplateManager.me().currentTemplateModule(moduleName);
		TplTaxonomyType type = module.getTaxonomyTypeByType(getType());
		BigInteger id = getParaToBigInteger("id");

		List<Taxonomy> taxonomys = TaxonomyQuery.me().findListByModuleAndTypeAsSort(moduleName, type.getName());

		if (id != null) {
			Taxonomy taxonomy = TaxonomyQuery.me().findById(id);
			setAttr("taxonomy", taxonomy);
			Content content = ContentQuery.me().findFirstByModuleAndObjectId(Consts.MODULE_MENU, taxonomy.getId());
			if (content != null) {
				setAttr("addToMenuSelete", "checked=\"checked\"");
			}
		}

		if (id != null && taxonomys != null) {
			ModelSorter.removeTreeBranch(taxonomys, id);
		}

		if (TplTaxonomyType.TYPE_SELECT.equals(type.getFormType())) {
			Page<Taxonomy> page = TaxonomyQuery.me().doPaginate(1, Integer.MAX_VALUE, getContentModule(), getType());
			ModelSorter.sort(page.getList());
			setAttr("page", page);
		} else if (TplTaxonomyType.TYPE_INPUT.equals(type.getFormType())) {
			Page<Taxonomy> page = TaxonomyQuery.me().doPaginate(getPageNumber(), getPageSize(), getContentModule(),
					getType());
			setAttr("page", page);
		}

		setAttr("module", module);
		setAttr("type", type);
		setAttr("taxonomys", taxonomys);

		String templateHtml = String.format("admin_taxonomy_index_%s_%s.html", moduleName, getType());
		for (int i = 0; i < 3; i++) {
			if (TemplateManager.me().existsFile(templateHtml)) {
				setAttr("include", TemplateManager.me().currentTemplatePath() + "/" + templateHtml);
				return;
			}
			templateHtml = templateHtml.substring(0, templateHtml.lastIndexOf("_")) + ".html";
		}

		setAttr("include", "_index_include.html");
	}

	public void save() {
		Taxonomy m = getModel(Taxonomy.class);

		if (StringUtils.isBlank(m.getTitle())) {
			renderAjaxResultForError("名称不能为空！");
			return;
		}

		if (StringUtils.isBlank(m.getSlug())) {
			renderAjaxResultForError("别名不能为空！");
			return;
		}

		if (StringUtils.isNumeric(m.getSlug())) {
			renderAjaxResultForError("别名不能全为数字！");
			return;
		}

		// getModel是jfinal通过model.put()设置属性的，不用调用setXXX设置。
		if (m.getSlug() != null) {
			// setSlug内部做了些格式化判断
			m.setSlug(m.getSlug());
		}

		Taxonomy dbTaxonomy = TaxonomyQuery.me().findBySlugAndModule(m.getSlug(), m.getContentModule());
		if (m.getId() != null && dbTaxonomy != null && m.getId().compareTo(dbTaxonomy.getId()) != 0) {
			renderAjaxResultForError("别名已经存在！");
			return;
		}

		if (m.saveOrUpdate()) {

			boolean addToMenu = getParaToBoolean("addToMenu", false);
			if (addToMenu) {
				Content content = ContentQuery.me().findFirstByModuleAndObjectId(Consts.MODULE_MENU, m.getId());
				if (content == null) {
					content = new Content();
					content.setModule(Consts.MODULE_MENU);
				}

				content.setOrderNumber(0l);
				content.setText(m.getUrl());
				content.setTitle(m.getTitle());
				content.setObjectId(m.getId());
				content.saveOrUpdate();

			} else {
				Content content = ContentQuery.me().findFirstByModuleAndObjectId(Consts.MODULE_MENU, m.getId());
				if (content != null) {
					content.delete();
				}
			}

			m.updateContentCount();

		}
		renderAjaxResultForSuccess("ok");
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		final BigInteger id = getParaToBigInteger("id");
		if (id == null) {
			renderAjaxResultForError();
			return;
		}

		boolean deleted = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (TaxonomyQuery.me().deleteById(id)) {
					MappingQuery.me().deleteByTaxonomyId(id);

					Content content = ContentQuery.me().findFirstByModuleAndObjectId(Consts.MODULE_MENU, id);
					if (content != null) {
						content.delete();
					}

					return true;
				}

				return false;
			}
		});

		if (deleted) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

	public void setting() {
		String moduleName = getContentModule();
		TplModule module = TemplateManager.me().currentTemplateModule(moduleName);
		TplTaxonomyType type = module.getTaxonomyTypeByType(getType());

		final BigInteger id = getParaToBigInteger("id");
		Taxonomy taxonomy = TaxonomyQuery.me().findById(id);
		setAttr("taxonomy", taxonomy);
		setAttr("type", type);

		String templateHtml = String.format("admin_taxonomy_setting_%s_%s.html", moduleName, getType());
		for (int i = 0; i < 3; i++) {
			if (TemplateManager.me().existsFile(templateHtml)) {
				setAttr("include", TemplateManager.me().currentTemplatePath() + "/" + templateHtml);
				return;
			}
			templateHtml = templateHtml.substring(0, templateHtml.lastIndexOf("_")) + ".html";
		}
		setAttr("include", "_setting_include.html");
	}

	@Before(UCodeInterceptor.class)
	public void doSaveSettings() {

		final Map<String, String> metas = getMetas();
		final Taxonomy taxonomy = getModel(Taxonomy.class);

		boolean saved = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (!taxonomy.saveOrUpdate()) {
					return false;
				}

				if (metas != null) {
					for (Map.Entry<String, String> entry : metas.entrySet()) {
						taxonomy.saveOrUpdateMetadta(entry.getKey(), entry.getValue());
					}
				}

				return true;
			}
		});

		if (saved) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}

	}

}
