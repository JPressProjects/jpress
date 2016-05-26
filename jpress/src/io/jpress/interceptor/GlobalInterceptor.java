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
package io.jpress.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import io.jpress.core.Jpress;
import io.jpress.model.Option;

public class GlobalInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		if (Jpress.isInstalled() && Jpress.isLoaded()) {
			setGlobalAttrs(inv.getController());
		}

		inv.invoke();
	}

	private void setGlobalAttrs(Controller c) {

		c.setAttr("TPATH", c.getRequest().getContextPath() + Jpress.currentTemplate().getPath());

		Boolean cdnEnable = Option.findValueAsBool("cdn_enable");
		if (cdnEnable != null && cdnEnable == true) {
			String cdnDomain = Option.findValue("cdn_domain");
			if (cdnDomain != null && !"".equals(cdnDomain.trim())) {
				c.setAttr("CDN", cdnDomain);
			}
		}

		c.setAttr("WEB_NAME", Option.findValue("web_name"));
		c.setAttr("WEB_TITLE", Option.findValue("web_title"));
		c.setAttr("META_KEYWORDS", Option.findValue("meta_keywords"));
		c.setAttr("META_DESCRIPTION", Option.findValue("meta_description"));
	}

}
