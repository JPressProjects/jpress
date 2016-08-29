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
package io.jpress.searcher;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.plugin.search.ISearcher;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.template.TplModule;
import io.jpress.template.TemplateManager;
import io.jpress.utils.StringUtils;

public class DbSearcher implements ISearcher {

	@Override
	public void init() {

	}

	@Override
	public void addBean(SearcherBean bean) {

	}

	@Override
	public void deleteBean(String beanId) {

	}

	@Override
	public void updateBean(SearcherBean bean) {

	}

	@Override
	public Page<SearcherBean> search(String keyword, String module) {
		return search(keyword, module, 1, 10);
	}

	@Override
	public Page<SearcherBean> search(String keyword, String module, int pageNum, int pageSize) {

		String[] moduleStrings = null;
		if (StringUtils.isNotBlank(module)) {
			moduleStrings = new String[] { module };
		} else {
			List<TplModule> modules = TemplateManager.me().currentTemplateModules();
			if (modules == null || modules.size() == 0) {
				return null;
			}

			moduleStrings = new String[modules.size()];
			for (int i = 0; i < moduleStrings.length; i++) {
				moduleStrings[i] = modules.get(i).getName();
			}
		}

		Page<Content> cpage = ContentQuery.me().paginate(pageNum, pageSize, moduleStrings, keyword,
				Content.STATUS_NORMAL, null, null, null, null);

		if (cpage != null) {
			List<SearcherBean> datas = new ArrayList<SearcherBean>();
			for (Content c : cpage.getList()) {
				datas.add(new SearcherBean(c.getId().toString(), c.getTitle(), c.getSummary(), c.getText(), c.getUrl(),c.getCreated(), c));
			}

			return new Page<>(datas, cpage.getPageNumber(), cpage.getPageSize(), cpage.getTotalPage(),cpage.getTotalRow());
		}

		return null;

	}

}
