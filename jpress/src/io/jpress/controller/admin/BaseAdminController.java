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
package io.jpress.controller.admin;

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.JModel;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.model.User;

import com.jfinal.aop.Before;

@Before(AdminInterceptor.class)
public class BaseAdminController<M extends JModel<? extends JModel<?>>> extends JBaseCRUDController<M> {

	public User getLoginedUser() {
		return getAttr("user");
	}

	@Override
	public void delete() {
		if (!validateToken()) {
			renderAjaxResultForError("非法提交");
		}
		super.delete();
	}
}
