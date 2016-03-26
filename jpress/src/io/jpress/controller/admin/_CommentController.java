package io.jpress.controller.admin;

import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.Comment;
import io.jpress.template.Module;

import com.jfinal.plugin.activerecord.Page;

@UrlMapping(url="/admin/comment",viewPath ="/WEB-INF/admin/comment")
public class _CommentController extends BaseAdminController<Comment> {
	
	
	private String getContentModule(){
		String module = getPara("m") ;
		if(null == module || "".equals(module)){
			module = Module.ARTICLE;
		}
		return module;
	}
	
	private String getType(){
		return getPara("t") ;
	}
	
	@Override
	public Page<Comment> onPageLoad(int pageNumber, int pageSize) {
		return mDao.doPaginate(pageNumber, pageSize, getContentModule(),getType());
	}
	
	
}
