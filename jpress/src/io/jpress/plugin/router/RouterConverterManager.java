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
package io.jpress.plugin.router;

import java.util.ArrayList;
import java.util.List;

public class RouterConverterManager {

	List<IRouterConverter> converters = new ArrayList<IRouterConverter>();

	public void register(Class<? extends IRouterConverter> clazz) {
		for (IRouterConverter tc : converters) {
			if (tc.getClass() == clazz) {
				throw new RuntimeException(String.format("Class [%s] has registered", clazz.getName()));
			}
		}

		try {
			converters.add(clazz.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IRouterConverter match(String target) {
		for (IRouterConverter converter : converters) {
			if (converter.match(target))
				return converter;
		}
		return null;
	}

}
