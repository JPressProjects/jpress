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
package io.jpress.router.converter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jpress.Consts;
import io.jpress.model.Content;
import io.jpress.router.IRouterConverter;

public class ContentRouter implements IRouterConverter {

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response, Boolean[] bools) {
		String slug = tryToGetContentSlug(target);
		if (null != slug) {
			Content c = Content.DAO.findBySlug(slug);
			if (c != null && Consts.SYS_MODULE_PAGE.equals(c.getModule())) {
				bools[0] = true;
				return Consts.CONTENT_BASE_URL + target;
			}
		}

		return null;
	}

	private String tryToGetContentSlug(String target) {
		String newTarget = target.substring(1);
		if ("".equals(newTarget))
			return null;
		return newTarget.indexOf("/") == -1 ? newTarget : null;
	}

	public String getSettingType() {

		return null;
	}

}
