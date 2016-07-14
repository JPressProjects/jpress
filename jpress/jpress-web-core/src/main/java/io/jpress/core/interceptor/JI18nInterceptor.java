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
package io.jpress.core.interceptor;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Const;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.i18n.Res;
import com.jfinal.kit.StrKit;

public class JI18nInterceptor extends I18nInterceptor {

	@Override
	public void intercept(Invocation inv) {

		Controller c = inv.getController();
		String localeParaName = getLocaleParaName();
		String locale = c.getPara(localeParaName);

		if (StrKit.notBlank(locale)) {
			// change locale, write cookie
			c.setCookie(localeParaName, locale, Const.DEFAULT_I18N_MAX_AGE_OF_COOKIE);
		} else {
			// get locale from cookie and use the default locale if it is null
			locale = c.getCookie(localeParaName);
			if (StrKit.isBlank(locale))
				locale = I18n.toLocale(c.getRequest().getLocale());
		}

		Res res = I18n.use(getBaseName(), locale);
		c.setAttr(getResName(), res);

		inv.invoke();
	}

	@Override
	protected String getResName() {
		return "i18n";
	}

}
