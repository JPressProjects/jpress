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
import java.util.List;

import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.core.Jpress;
import io.jpress.core.db.Table;
import io.jpress.model.base.BaseUser;
import io.jpress.template.Module;

@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {
	private static final long serialVersionUID = 1L;
	public static final User DAO = new User();

	public static final String ROLE_ADMINISTRATOR = "administrator";
	public static final String STATUS_NORMAL = "normal";
	public static final String STATUS_FROZEN = "frozen";

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

	public long findAdminCount() {
		return doFindCount(" role = ? ", "administrator");
	}

	public boolean updateContentCount() {
		long count = 0;
		List<Module> modules = Jpress.currentTemplate().getModules();
		if (modules != null && !modules.isEmpty()) {
			for (Module m : modules) {
				long moduleCount = Content.DAO.findCountInNormalByModuleAndUserId(m.getName(), getId());
				count += moduleCount;
			}
		}

		this.setContentCount(count);
		return this.update();
	}

	public boolean updateCommentCount() {
		long count = Comment.DAO.findCountByUserIdInNormal(getId());
		this.setCommentCount(count);
		return this.update();
	}

}
