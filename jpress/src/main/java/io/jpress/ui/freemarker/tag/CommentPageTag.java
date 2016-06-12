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
package io.jpress.ui.freemarker.tag;

import io.jpress.core.render.freemarker.JTag;
import io.jpress.model.Content;

/**
 * @title Content 标签
 * @author Michael Yang （http://www.yangfuhai.com）
 * @version 1.0
 * @created 2016年2月19日
 * 
 *          使用方法：<br />
 *          <@jp_commentpage page="" pagesize="" module="article" orderby ><br>
 *          <br>
 *          <#list page.getList() as content><br>
 *          ${content.id} : ${content.title!} <br>
 *          </#list><br>
 *          <br>
 *          </@jp_commentpage>
 * 
 */
public class CommentPageTag extends JTag {

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
