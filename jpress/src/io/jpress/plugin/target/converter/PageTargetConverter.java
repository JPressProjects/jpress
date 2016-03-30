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
package io.jpress.plugin.target.converter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jpress.core.Jpress;
import io.jpress.model.Content;
import io.jpress.plugin.target.ItargetConverter;

public class PageTargetConverter implements ItargetConverter {

	@Override
	public boolean match(String target) {
		if (Jpress.isInstalled()) {
			String slug = tryToGetContentSlug(target);
			return Content.DAO.findBySlug(slug) != null;
		}
		return false;
	}

	@Override
	public String converter(String target, HttpServletRequest request, HttpServletResponse response) {
		return "/c" + target;
	}

	private String tryToGetContentSlug(String target) {
		String newTarget = target.substring(1);
		return newTarget.indexOf("/") == -1 ? newTarget : null;
	}

}
