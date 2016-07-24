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

import java.io.IOException;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;

public class SearcherKit {

	private static final Log logger = Log.getLog(SearcherKit.class);
	private static ISearcher mSearcher;

	static void init(ISearcher mSearcher) {
		SearcherKit.mSearcher = mSearcher;
	}

	static void add(SearcherBean bean) throws IOException {
		checkSearcher();
		mSearcher.addBean(bean);
	}

	public static void delete(String beanId) throws IOException {
		checkSearcher();
		mSearcher.deleteBean(beanId);
	}

	public static void update(SearcherBean bean) throws IOException {
		checkSearcher();
		mSearcher.deleteBean(bean.getSid());
		mSearcher.addBean(bean);
	}

	/**
	 * 执行搜索
	 * 
	 * @param keyword
	 * @return List<SearcherBean>
	 */
	public static Page<SearcherBean> search(String keyword) {
		checkSearcher();
		try {
			return mSearcher.search(keyword,null);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Page<SearcherBean> search(String keyword,String module) {
		checkSearcher();
		try {
			return mSearcher.search(keyword,module);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Page<SearcherBean> search(String queryString, int pageNum, int pageSize) {
		checkSearcher();
		return mSearcher.search(queryString, null,pageNum, pageSize);
	}
	
	public static Page<SearcherBean> search(String queryString,String module, int pageNum, int pageSize) {
		checkSearcher();
		return mSearcher.search(queryString, module,pageNum, pageSize);
	}

	public static void checkSearcher() {
		if (mSearcher == null) {
			throw new RuntimeException("must init searcher before,please invoke SearchFactory.use() to init.");
		}
	}

}
