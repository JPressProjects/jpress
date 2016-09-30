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

import java.util.List;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;

import io.jpress.utils.ClassUtils;

public class SearcherPlugin implements IPlugin {

	static final Log log = Log.getLog(SearcherPlugin.class);

	private static ISearcher mSearcher;

	public static void initSearcher(Class<? extends ISearcher> clazz) {
		try {
			mSearcher = (ISearcher) clazz.newInstance();
			mSearcher.init();
			SearcherKit.init(mSearcher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean start() {
		List<Class<ISearcher>> list = ClassUtils.scanSubClass(ISearcher.class, true);

		if (list == null || list.isEmpty()) {
			log.error("cant scan ISearcher implement class in class path.");
			return true;
		}

		if (list.size() > 1) {
			log.warn("there are too many searcher");
		}

		initSearcher(list.get(0));

		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
