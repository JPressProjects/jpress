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
package io.jpress.model;

import java.math.BigInteger;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseUser;

@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {
	private static final long serialVersionUID = 1L;
	public static final User DAO = new User();

	public String ROLE_ADMINISTRATOR = "administrator";

	public Page<User> doPaginateWithContent(int pageNumber, int pageSize) {
		String select = "select u.*,count(c.id) as content_count ";
		String sqlExceptSelect = "from user u left join content c on u.id = c.user_id group by u.id";
		return paginate(pageNumber, pageSize, true, select, sqlExceptSelect);
	}


	public User findUserById(final BigInteger userId) {
		return getCache(userId, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(userId);
			}
		});
	}

	public User findUserByEmail(final String email) {
		return getCache(email, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("email = ?", email);
			}
		});
	}

	public User findUserByUsername(final String username) {
		return getCache(username, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("username = ?", username);
			}
		});
	}

	public User findUserByPhone(final String phone) {
		return getCache(phone, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("phone = ?", phone);
			}
		});
	}

	public boolean isAdministrator() {
		return "administrator".equals(getRole());
	}

}
