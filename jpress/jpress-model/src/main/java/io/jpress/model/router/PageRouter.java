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
package io.jpress.model.router;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jpress.Consts;
import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.utils.StringUtils;

public class PageRouter extends RouterConverter {

	public static String getRouter(Content content) {
		String url = SLASH + content.getSlug();

		if (enalbleFakeStatic()) {
			url += getFakeStaticSuffix();
		}
		return url;
	}

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response) {

		String[] targetDirs = parseTarget(target);
		if (targetDirs == null || targetDirs.length != 1) {
			return null;
		}

		String slug = targetDirs[0];
		Content content = ContentQuery.me().findBySlug(StringUtils.urlDecode(slug));
		if (null != content && Consts.MODULE_PAGE.equals(content.getModule())) {
			return Consts.ROUTER_CONTENT + SLASH + slug;
		}

		return null;
	}

}
