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
package io.jpress.model.query;

import java.math.BigInteger;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.model.User;
import io.jpress.template.Module;
import io.jpress.template.TemplateUtils;

public class UserQuery extends JBaseQuery {
	private static User MODEL = new User();
	
	public static User findFirstFromMetadata(String key,Object value){
		return MODEL.findFirstFromMetadata( key, value);
	}
	
	public static Page<User> paginate(int pageNumber, int pageSize){
		return MODEL.doPaginate(pageNumber, pageSize);
	}
	
	public static long findCount(){
		return MODEL.doFindCount();
	}

	public static long findAdminCount() {
		return MODEL.doFindCount(" role = ? ", "administrator");
	}

	public static User findById(final BigInteger userId) {
		return MODEL.getCache(userId, new IDataLoader() {
			@Override
			public Object load() {
				return MODEL.findById(userId);
			}
		});
	}

	public static User findUserByEmail(final String email) {
		return MODEL.getCache(email, new IDataLoader() {
			@Override
			public Object load() {
				return MODEL.doFindFirst("email = ?", email);
			}
		});
	}

	public static User findUserByUsername(final String username) {
		return MODEL.getCache(username, new IDataLoader() {
			@Override
			public Object load() {
				return MODEL.doFindFirst("username = ?", username);
			}
		});
	}

	public static User findUserByPhone(final String phone) {
		return MODEL.getCache(phone, new IDataLoader() {
			@Override
			public Object load() {
				return MODEL.doFindFirst("phone = ?", phone);
			}
		});
	}

	public static boolean updateContentCount(User user) {
		long count = 0;
		List<Module> modules = TemplateUtils.currentTemplate().getModules();
		if (modules != null && !modules.isEmpty()) {
			for (Module m : modules) {
				long moduleCount = ContentQuery.findCountInNormalByModuleAndUserId(m.getName(), user.getId());
				count += moduleCount;
			}
		}

		user.setContentCount(count);
		return user.update();
	}

	public static boolean updateCommentCount(User user) {
		long count = CommentQuery.findCountByUserIdInNormal(user.getId());
		user.setCommentCount(count);
		return user.update();
	}

}
