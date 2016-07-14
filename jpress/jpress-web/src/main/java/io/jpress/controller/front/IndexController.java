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
package io.jpress.controller.front;

import io.jpress.Consts;
import io.jpress.core.addon.HookInvoker;
import io.jpress.core.cache.ActionCache;
import io.jpress.model.query.OptionQuery;
import io.jpress.router.RouterMapping;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/")
public class IndexController extends BaseFrontController {

	@ActionCache
	public void index() {
		try {
			onRenderBefore();
			doRender();
		} finally {
			onRenderAfter();
		}
	}

	private void doRender() {
		String page = getPara();
		if (StringUtils.isNotBlank(page)) {
			render("page_" + page + ".html");
		} else {
			setGlobleAttrs();
			render("index.html");
		}
	}
	
	private void setGlobleAttrs() {
		String title = OptionQuery.me().findValue("seo_index_title");
		String keywords = OptionQuery.me().findValue("seo_index_keywords");
		String description = OptionQuery.me().findValue("seo_index_description");
		
		if(StringUtils.isNotBlank(title)){
			setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, title);
		}
		
		if(StringUtils.isNotBlank(keywords)){
			setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, keywords);
		}
		
		if(StringUtils.isNotBlank(description)){
			setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, description);
		}
	}


	private void onRenderBefore() {
		HookInvoker.indexRenderBefore(this);
	}

	private void onRenderAfter() {
		HookInvoker.indexRenderAfter(this);
	}

}
