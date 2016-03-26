package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Content;

import java.util.List;

/**
 * @title Content 标签
 * @author Michael Yang （http://www.yangfuhai.com）
 * @version 1.0
 * @created 2016年2月19日
 * 
 * 
 *          使用方法：<br />
 *          <@jp_clist orderBy="" keyword="Jpress" page="" tag="tag1,xxx"
 *          pagesize="" typeid="1,2" module="article,bbs"
 *          style="article,video,audio" userid="123" parentid="1" userid="" ><br>
 * <br>
 *          <#list contents as content><br>
 *          ${content.id} : ${content.title!} <br>
 *          <//#list><br>
 * <br>
 *          </@jp_clist>
 * 
 * 
 *          orderBy 的值有：views,lastpost,created,vote_up,vote_down
 * 
 * 
 */
public class ContentTag extends JTag {

	@Override
	public void onRender() {

		String orderBy = getParam("orderby");
		String keyword = getParam("keyword");

		int pageNumber = getParamToInt("page", 1);
		int pageSize = getParamToInt("pagesize", 10);

		Long[] typeIds = getParamToLongArray("typeid");
		String[] typeSlugs = getParamToStringArray("typeslug");
		String[] tags = getParamToStringArray("tag");
		String[] modules = getParamToStringArray("module");
		String[] styles = getParamToStringArray("style");
		String[] slugs = getParamToStringArray("slug");
		Integer[] userIds = getParamToIntArray("userid");
		Integer[] parentIds = getParamToIntArray("parentid");

		List<Content> data = Content.DAO.findList(pageNumber, pageSize,
				orderBy, keyword, typeIds, typeSlugs, modules, styles, slugs,
				userIds, parentIds, tags);
		
		setVariable("contents", data);

		renderBody();
	}

}
