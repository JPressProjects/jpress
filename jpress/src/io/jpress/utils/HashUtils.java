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
package io.jpress.utils;

import io.jpress.model.User;

import java.util.UUID;

import com.jfinal.kit.HashKit;

public class HashUtils extends HashKit {

	public static String salt() {
		int random = (int) (10 + (Math.random() * 10));
		return UUID.randomUUID().toString().replace("-", "").substring(random);
	}

	public static String md5WithSalt(String text, String salt) {
		return md5(md5(text) + salt).substring(0, 20);
	}

	public static boolean verlifyUser(User user, String password) {
		return user.getPassword().equals(md5WithSalt(password, user.getSalt()));
	}

	public static String generateUcode(User user) {
		return md5(user.getSalt() + user.getId());
	}

	public static void main(String[] args) {
		// System.out.println(md5WithSalt("123456","123"));
		// 51e34a82801b3a98396e, d632686d14972f3
		System.out.println(md5WithSalt("xxx", "d632686d14972f3"));
	}

}
