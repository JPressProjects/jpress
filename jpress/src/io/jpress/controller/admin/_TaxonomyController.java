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

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Mapping;
import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.template.Module;
import io.jpress.template.Module.TaxonomyType;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/admin/taxonomy", viewPath = "/WEB-INF/admin/taxonomy")
@Before(ActionCacheClearInterceptor.class)
public class _TaxonomyController extends JBaseCRUDController<Taxonomy> {

	private String getContentModule() {
		return getPara("m");
	}

	private String getType() {
		return getPara("t");
	}

	public void index() {
		String moduleName = getContentModule();
		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		TaxonomyType type = module.getTaxonomyTypeByType(getType());
		BigInteger id = getParaToBigInteger("id");

		List<Taxonomy> taxonomys = Taxonomy.DAO.findListByModuleAndTypeAsSort(moduleName, type.getName());

		if (id != null) {
			setAttr("taxonomy", Taxonomy.DAO.findById(id));
		}
		
		if (id != null && taxonomys != null) {
			ModelSorter.removeTreeBranch(taxonomys, id);
		}

		setAttr("module", module);
		setAttr("type", type);
		setAttr("taxonomys", taxonomys);

		super.index();
	}

	@Override
	public boolean onModelSaveBefore(Taxonomy m) {
		
		return super.onModelSaveBefore(m);
	}
	
	

	public void save() {
		Taxonomy m = getModel(Taxonomy.class);
		
		if(!StringUtils.isNotBlank(m.getTitle())){
			renderAjaxResultForError("名称不能为空！");
			return;
		}
		
		if(!StringUtils.isNotBlank(m.getSlug())){
			renderAjaxResultForError("别名不能为空！");
			return;
		}
		
		Taxonomy dbTaxonomy = Taxonomy.DAO.findBySlugAndModule(m.getSlug(), m.getContentModule());
		if(m.getId() != null && dbTaxonomy!=null && m.getId().compareTo(dbTaxonomy.getId()) != 0){
			renderAjaxResultForError("别名已经存在！");
			return;
		}
		
		m.saveOrUpdate();
		renderAjaxResultForSuccess("ok");
	}

	@Override
	public Page<Taxonomy> onIndexDataLoad(int pageNumber, int pageSize) {
		Page<Taxonomy> page = mDao.doPaginate(pageNumber, pageSize, getContentModule(), getType());
		ModelSorter.sort(page.getList());
		return page;
	}

	@Before(UCodeInterceptor.class)
	public void delete() {
		final BigInteger id = getParaToBigInteger("id");
		if (id == null) {
			renderAjaxResultForError();
			return;
		}

		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				mDao.deleteById(id);
				Mapping.DAO.deleteByTaxonomyId(id);
				return true;
			}
		});

	}

}
