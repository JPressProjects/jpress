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

import io.jpress.ui.freemarker.tag.Tags;

public class GlobalInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		
		Controller c = inv.getController();
		c.setAttr("c", c.getPara("c"));
		c.setAttr("p", c.getPara("p"));
		c.setAttr("m", c.getPara("m"));
		c.setAttr("t", c.getPara("t"));
		c.setAttr("page", c.getPara("page"));
		
		
		Tags.initInInterceptor(inv);
		
		inv.invoke();

	}

}
