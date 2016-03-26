package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Content;

/**
 * @title Content 标签
 * @author Michael Yang （http://www.yangfuhai.com）
 * @version 1.0
 * @created 2016年2月19日
 * 
 *          使用方法：<br />
 *          <@jp_cpage  page="" pagesize=""  module="article" orderby ><br>
 * <br>
 *          <#list page.getList() as content><br>
 *          ${content.id} : ${content.title!} <br>
 *          </#list><br>
 * <br>
 *          </@jp_cpage>
 * 
 */
public class ContentPageTag extends JTag {

	@Override
	public void onRender() {

		int pageNumber = getParamToInt("page", 1);
		int pageSize = getParamToInt("pagesize", 10);

		String module = getParam("module");
		String orderby = getParam("orderby");
		String status = getParam("status", Content.STATUS_NORMAL);
		
		setVariable("page", Content.DAO.doPaginateByModuleAndStatus(pageNumber, pageSize, module, status));

		renderBody();
	}

}
