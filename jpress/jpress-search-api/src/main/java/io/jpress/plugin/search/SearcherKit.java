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
package io.jpress.plugin.search;

import com.jfinal.plugin.activerecord.Page;

public class SearcherKit {

	private static ISearcher mSearcher;

	static void init(ISearcher mSearcher) {
		SearcherKit.mSearcher = mSearcher;
	}

	public static void add(SearcherBean bean) {
		checkSearcher();
		mSearcher.addBean(bean);
	}

	public static void delete(String beanId) {
		checkSearcher();
		mSearcher.deleteBean(beanId);
	}

	public static void update(SearcherBean bean) {
		checkSearcher();
		mSearcher.updateBean(bean);
	}

	/**
	 * 执行搜索
	 * 
	 * @param keyword
	 * @return List<SearcherBean>
	 */
	public static Page<SearcherBean> search(String keyword) {
		checkSearcher();
		return mSearcher.search(keyword, null);
	}

	public static Page<SearcherBean> search(String keyword, String module) {
		checkSearcher();
		return mSearcher.search(keyword, module);
	}

	public static Page<SearcherBean> search(String queryString, int pageNum, int pageSize) {
		checkSearcher();
		return mSearcher.search(queryString, null, pageNum, pageSize);
	}

	public static Page<SearcherBean> search(String queryString, String module, int pageNum, int pageSize) {
		checkSearcher();
		return mSearcher.search(queryString, module, pageNum, pageSize);
	}

	public static void checkSearcher() {
		if (mSearcher == null) {
			throw new RuntimeException("must init searcher before,please invoke SearchFactory.use() to init.");
		}
	}

}
