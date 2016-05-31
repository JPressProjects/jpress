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

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

import io.jpress.Consts;
import io.jpress.core.JBaseCRUDController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.User;
import io.jpress.utils.HashUtils;

@UrlMapping(url = "/admin/user", viewPath = "/WEB-INF/admin/user")
@Before(ActionCacheClearInterceptor.class)
public class _UserController extends JBaseCRUDController<User> {
	
	
	@Override
	public Page<User> onIndexDataLoad(int pageNumber, int pageSize) {
		return mDao.doPaginateWithContent(pageNumber, pageSize);
	}
	
	
	@Override
	public boolean onModelSaveBefore(User m) {
		
		//修改了密码
		if(m.getId() != null && m.getPassword() != null){
			User dbUser = User.DAO.findById(m.getId());
			m.setSalt(dbUser.getSalt());
			String password = HashUtils.md5WithSalt(m.getPassword(), dbUser.getSalt());
			m.setPassword(password);
		}
		
		//新建用户
		if(m.getId() == null && m.getPassword() != null){
			String salt = HashUtils.salt();
			m.setSalt(salt);
			
			String password = HashUtils.md5WithSalt(m.getPassword(), salt);
			m.setPassword(password);
		}
		
		return super.onModelSaveBefore(m);
	}

	public void info(){
		User user = getAttr(Consts.ATTR_USER);
		setAttr("user", user);
		render("edit.html");
	}
	
	
	

}
