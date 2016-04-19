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
package io.jpress.ui.tag;

import io.jpress.core.ui.JTag;
import io.jpress.model.Content;

import java.util.List;

/**
 * @title Contents 标签
 * 
 *        使用方法：<br />
 *        <@jp_contents orderBy="" keyword="Jpress" page="" tag="tag1,xxx"
 *        pagesize="" typeid="1,2" module="article,bbs" style=
 *        "article,video,audio" userid="123" parentid="1" userid="" ><br>
 *        <br>
 *        <#list contents as content><br>
 *        ${content.id} : ${content.title!} <br>
 *        <//#list><br>
 *        <br>
 *        </@jp_contents>
 * 
 * 
 *        orderBy 的值有：views,lastpost,created,vote_up,vote_down
 * 
 * 
 */
public class ContentsTag extends JTag {

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
		String[] flags = getParamToStringArray("flag");
		String[] slugs = getParamToStringArray("slug");
		Integer[] userIds = getParamToIntArray("userid");
		Integer[] parentIds = getParamToIntArray("parentid");

		List<Content> data = Content.DAO.findListInNormal(pageNumber, pageSize, orderBy, keyword, typeIds, typeSlugs,
				modules, styles, flags, slugs, userIds, parentIds, tags);

		setVariable("contents", data);

		renderBody();
	}

}
