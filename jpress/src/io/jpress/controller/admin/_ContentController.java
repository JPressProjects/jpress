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
package io.jpress.controller.admin;

import io.jpress.core.JTokenInterceptor;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.Mapping;
import io.jpress.model.Taxonomy;
import io.jpress.template.Module;
import io.jpress.template.Module.TaxonomyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

@UrlMapping(url = "/admin/content", viewPath = "/WEB-INF/admin/content")
public class _ContentController extends BaseAdminController<Content> {

	private String getModuleName() {
		String module = getPara("m");
		if (null == module || "".equals(module.trim()))
			module = Module.ARTICLE;

		return module;
	}

	private String getStatus() {
		return getPara("s");
	}

	@Override
	public void index() {
		setAttr("module", Jpress.currentTemplate().getModuleByName(getModuleName()));
//		setAttr("template", value)
		
		

		setAttr("delete_count", mDao.findCountByModuleAndStatus(getModuleName(),
				Content.STATUS_DELETE));
		setAttr("draft_count", mDao.findCountByModuleAndStatus(getModuleName(),
				Content.STATUS_DRAFT));
		setAttr("normal_count", mDao.findCountByModuleAndStatus(getModuleName(),
				Content.STATUS_NORMAL));
		setAttr("count", mDao.findCountInNormalByModule(getModuleName()));

		super.index();
	}

	@Override
	public Page<Content> onPageLoad(int pageNumber, int pageSize) {
		if (getStatus() != null && !"".equals(getStatus().trim())) {
			return mDao.doPaginateByModuleAndStatus(pageNumber, pageSize,
					getModuleName(), getStatus());
		}
		return mDao.doPaginateInNormalByModule(pageNumber, pageSize,
				getModuleName());
	}

	@Before(UCodeInterceptor.class)
	public void trash() {
		long id = getParaToLong("id");
		Content c = Content.DAO.findById(id);
		if (c != null) {
			c.setStatus(Content.STATUS_DELETE);
			c.saveOrUpdate();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void batchTrash() {
		Long[] ids = getParaValuesToLong("dataItem");
		int count = mDao.batchTrash(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void restore() {
		long id = getParaToLong("id");
		Content c = Content.DAO.findById(id);
		if (c != null && c.isDelete()) {
			c.setStatus(Content.STATUS_DRAFT);
			c.setModified(new Date());
			c.saveOrUpdate();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("restore error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		long id = getParaToLong("id");
		Content c = Content.DAO.findById(id);
		if (c != null && c.isDelete()) {
			c.delete();
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("restore error!");
		}
	}

	@Override
	public void edit() {
		keepPara();
		String moduleName = getModuleName();
		setAttr("m", moduleName);

		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		setAttr("module", module);

		String id = getPara("id");
		if (id != null) {
			setAttr("content", mDao.findById(id));
		}
		render("edit.html");
	}

	private Content getContent() {
		Content content = getModel(Content.class);

		String text = content.getText();
		if (null != text && !"".equals(text)) {
			Document document = Jsoup.parse(text);
			if (null != document && document.body() != null) {
				content.setText(document.body().html().toString());
			}
		}

		content.setCreated(new Date());
		content.setModified(new Date());
		content.setUserId(getLoginedUser().getId());

		return content;
	}

	public List<Long> getOrCreateTaxonomyIds(String moduleName) {
		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		List<TaxonomyType> types = module.getTaxonomyTypes();
		List<Long> tIds = new ArrayList<Long>();
		for (TaxonomyType type : types) {
			if (TaxonomyType.TYPE_INPUT.equals(type.getFormType())) {
				String params = getPara("_" + type.getName());
				String[] titles = params.split(",");
				if (titles != null && titles.length > 0) {
					List<Taxonomy> list = Taxonomy.DAO.findListByModuleAndType(
							moduleName, type.getName());
					for (String title : titles) {
						long id = getIdFromList(title, list);
						if (id == 0) {
							Taxonomy taxonomy = new Taxonomy();
							taxonomy.setTitle(title);
							taxonomy.setContentModule(moduleName);
							taxonomy.setType(type.getName());
							taxonomy.save();
							
							id = taxonomy.getId();
						}
						tIds.add(id);
					}
				}
			} else if (TaxonomyType.TYPE_SELECT.equals(type.getFormType())) {
				Long[] ids = getParaValuesToLong("_" + type.getName());
				if (ids != null && ids.length > 0)
					tIds.addAll(Arrays.asList(ids));
			}
		}
		return tIds;
	}

	private long getIdFromList(String string, List<Taxonomy> list) {
		for (Taxonomy taxonomy : list) {
			if (string.equals(taxonomy.getTitle()))
				return taxonomy.getId();
		}
		return 0;
	}

	@Before(JTokenInterceptor.class)
	@Override
	public void save() {
		Content content = getContent();
		content.saveOrUpdate();

		List<Long> ids = getOrCreateTaxonomyIds(content.getModule());
		Mapping.DAO.doBatchUpdate(content.getId(), ids.toArray(new Long[0]));

		renderAjaxResultForSuccess("save ok");
	}

}
