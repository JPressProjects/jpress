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

import java.math.BigInteger;

import com.jfinal.aop.Invocation;

import io.jpress.Consts;
import io.jpress.model.User;
import io.jpress.model.query.UserQuery;
import io.jpress.utils.CookieUtils;
import io.jpress.utils.StringUtils;

public class InterUtils {

	public static User tryToGetUser(Invocation inv) {

		String userId = CookieUtils.get(inv.getController(), Consts.COOKIE_LOGINED_USER);
		if (StringUtils.isNotBlank(userId)) {
			return UserQuery.me().findById(new BigInteger(userId));
		}

		return null;
	}

}
