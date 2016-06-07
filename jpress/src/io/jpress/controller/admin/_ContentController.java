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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.Consts;
import io.jpress.core.AjaxResult;
import io.jpress.core.JBaseCRUDController;
import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.model.Mapping;
import io.jpress.model.Option;
import io.jpress.model.Taxonomy;
import io.jpress.model.User;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.message.listener.Actions;
import io.jpress.router.converter.ContentRouter;
import io.jpress.template.Module;
import io.jpress.template.Module.TaxonomyType;
import io.jpress.utils.StringUtils;

@UrlMapping(url = "/admin/content", viewPath = "/WEB-INF/admin/content")
@Before(ActionCacheClearInterceptor.class)
public class _ContentController extends JBaseCRUDController<Content> {

	private String getModuleName() {
		return getPara("m");
	}

	private String getStatus() {
		return getPara("s");
	}

	@Override
	public void index() {
		setAttr("module", Jpress.currentTemplate().getModuleByName(getModuleName()));
		setAttr("delete_count", mDao.findCountByModuleAndStatus(getModuleName(), Content.STATUS_DELETE));
		setAttr("draft_count", mDao.findCountByModuleAndStatus(getModuleName(), Content.STATUS_DRAFT));
		setAttr("normal_count", mDao.findCountByModuleAndStatus(getModuleName(), Content.STATUS_NORMAL));
		setAttr("count", mDao.findCountInNormalByModule(getModuleName()));

		super.index();
	}

	@Override
	public Page<Content> onIndexDataLoad(int pageNumber, int pageSize) {
		if (getStatus() != null && !"".equals(getStatus().trim())) {
			return mDao.doPaginateByModuleAndStatus(pageNumber, pageSize, getModuleName(), getStatus());
		}
		return mDao.doPaginateByModuleInNormal(pageNumber, pageSize, getModuleName());
	}

	@Before(UCodeInterceptor.class)
	public void trash() {
		Content c = Content.DAO.findById(getParaToBigInteger("id"));
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
		BigInteger[] ids = getParaValuesToBigInteger("dataItem");
		int count = mDao.batchTrash(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void batchDelete() {
		BigInteger[] ids = getParaValuesToBigInteger("dataItem");
		int count = mDao.batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("trash error!");
		}
	}

	@Before(UCodeInterceptor.class)
	public void restore() {
		BigInteger id = getParaToBigInteger("id");
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
		BigInteger id = getParaToBigInteger("id");
		final Content c = Content.DAO.findById(id);
		if (c != null && c.isDelete()) {
			boolean isSuccess = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					c.delete();
					Mapping.DAO.deleteByContentId(c.getId());
					return true;
				}
			});

			if (isSuccess) {
				renderAjaxResultForSuccess();
				return;
			}
		}
		renderAjaxResultForError();
	}

	@Override
	public void edit() {

		String moduleName = getModuleName();
		BigInteger contentId = getParaToBigInteger("id");

		Content content = Content.DAO.findById(contentId);
		if (content != null) {
			setAttr("content", content);
			moduleName = content.getModule();
		}

		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		setAttr("module", module);

		String _editor = getCookie("_editor", "tinymce");
		setAttr("_editor", _editor);

		setAttr("urlPreffix", ContentRouter.getContentRouterPreffix(module));
		setAttr("urlSuffix", ContentRouter.getContentRouterSuffix(module));
		
		String routerType = Option.findValue("router_content_type");
		if (ContentRouter.TYPE_DYNAMIC_ID.equals(routerType)) {
			setAttr("slugDisplay", " style=\"display: none\"");
		}

		super.edit();
	}

	public void changeEditor() {
		String name = getPara();
		setCookie("_editor", name, Integer.MAX_VALUE);
		renderAjaxResultForSuccess();
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

		if (content.getCreated() == null) {
			content.setCreated(new Date());
		}
		content.setModified(new Date());

		User user = getAttr(Consts.ATTR_USER);
		content.setUserId(user.getId());

		return content;
	}

	public List<BigInteger> getOrCreateTaxonomyIds(String moduleName) {
		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		List<TaxonomyType> types = module.getTaxonomyTypes();
		List<BigInteger> tIds = new ArrayList<BigInteger>();
		for (TaxonomyType type : types) {
			if (TaxonomyType.TYPE_INPUT.equals(type.getFormType())) {
				String data = getPara("_" + type.getName());
				if (!StringUtils.isNotEmpty(data)) {
					continue;
				}
				String[] titles = data.split(",");
				if (titles != null && titles.length > 0) {
					List<Taxonomy> list = Taxonomy.DAO.findListByModuleAndType(moduleName, type.getName());
					for (String title : titles) {
						BigInteger id = getIdFromList(title, list);
						if (id == null) {
							Taxonomy taxonomy = new Taxonomy();
							taxonomy.setTitle(title);
							taxonomy.setSlug(title);
							taxonomy.setContentModule(moduleName);
							taxonomy.setType(type.getName());
							taxonomy.save();
							id = taxonomy.getId();
						}
						tIds.add(id);
					}
				}
			} else if (TaxonomyType.TYPE_SELECT.equals(type.getFormType())) {
				BigInteger[] ids = getParaValuesToBigInteger("_" + type.getName());
				if (ids != null && ids.length > 0)
					tIds.addAll(Arrays.asList(ids));
			}
		}
		return tIds;
	}

	private BigInteger getIdFromList(String string, List<Taxonomy> list) {
		for (Taxonomy taxonomy : list) {
			if (string.equals(taxonomy.getSlug()))
				return taxonomy.getId();
		}
		return null;
	}

	@Before(UCodeInterceptor.class)
	@Override
	public void save() {

		final Content content = getContent();

		String slug = content.getSlug();
		if (!StringUtils.isNotBlank(slug)) {
			slug = content.getTitle();
		}

		if (slug != null) {
			slug = slug.replaceAll("(\\s+)|(\\.+)|(。+)|(…+)|[\\$,，？\\-?、；;:!]", "_");
			slug = slug.replaceAll("(?!_)\\pP|\\pS", "");

			content.setSlug(slug);
		}

		String username = getPara("username");
		if (StringUtils.isNotBlank(username)) {
			User user = User.DAO.findUserByUsername(username);
			if (user == null) {
				renderAjaxResultForError("系统没有该用户：" + username);
				return;
			}
			content.setUserId(user.getId());
		}

		Content dbContent = mDao.findBySlug(content.getSlug());
		if (dbContent != null && dbContent.getId().compareTo(content.getId()) != 0) {
			renderAjaxResultForError();
			return;
		}

		boolean saved = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				Content oldContent = null;
				if (content.getId() != null) {
					oldContent = mDao.findById(content.getId());
				}

				content.saveOrUpdate();
				List<BigInteger> ids = getOrCreateTaxonomyIds(content.getModule());
				Mapping.DAO.doBatchUpdate(content.getId(), ids.toArray(new BigInteger[0]));

				MessageKit.sendMessage(Actions.CONTENT_COUNT_UPDATE, ids.toArray(new BigInteger[] {}));

				if (oldContent != null && oldContent.getTaxonomys() != null) {
					List<Taxonomy> taxonomys = oldContent.getTaxonomys();
					BigInteger[] taxonomyIds = new BigInteger[taxonomys.size()];
					for (int i = 0; i < taxonomys.size(); i++) {
						taxonomyIds[i] = taxonomys.get(i).getId();
					}
					MessageKit.sendMessage(Actions.CONTENT_COUNT_UPDATE, taxonomyIds);
				}

				return true;
			}
		});

		if (!saved) {
			renderAjaxResultForError();
			return;
		}

		AjaxResult ar = new AjaxResult();
		ar.setErrorCode(0);
		ar.setData(content.getId());
		renderAjaxResult("save ok", 0, content.getId());
	}

}
