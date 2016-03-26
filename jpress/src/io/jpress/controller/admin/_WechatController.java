package io.jpress.controller.admin;

import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.model.Content;
import io.jpress.template.Module;

import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

@UrlMapping(url = "/admin/wechat", viewPath = "/WEB-INF/admin/wechat")
public class _WechatController extends BaseAdminController<Content> {

	private String getModule() {
		return "wechatReplay";
	}
	
	private String getStatus() {
		return getPara("s");
	}

	@Override
	public void index() {
		setAttr("module", getModule());

		setAttr("delete_count", mDao.findCountByModuleAndStatus(getModule(),Content.STATUS_DELETE));
		setAttr("draft_count", mDao.findCountByModuleAndStatus(getModule(),Content.STATUS_DRAFT));
		setAttr("normal_count", mDao.findCountByModuleAndStatus(getModule(),Content.STATUS_NORMAL));
		setAttr("count", mDao.findCountInNormalByModule(getModule()));

		super.index();
	}

	@Override
	public Page<Content> onPageLoad(int pageNumber, int pageSize) {
		if (getStatus() != null && !"".equals(getStatus().trim())) {
			return mDao.doPaginateByModuleAndStatus(pageNumber, pageSize,getModule(), getStatus());
		}
		return mDao.doPaginateInNormalByModule(pageNumber, pageSize,getModule());
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
		String moduleName = getModule();
		setAttr("m", moduleName);

		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		setAttr("module", module);

		String id = getPara("id");
		if (id != null) {
			setAttr("content", mDao.findById(id));
		}
		render("edit.html");
	}





}
