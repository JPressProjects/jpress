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
package io.jpress.plugin.target;

import java.util.ArrayList;
import java.util.List;

public class TargetConverterManager {

	List<ItargetConverter> converters = new ArrayList<ItargetConverter>();

	public void register(Class<? extends ItargetConverter> clazz) {
		for (ItargetConverter tc : converters) {
			if (tc.getClass() == clazz) {
				throw new RuntimeException(String.format(
						"Class [%s] has registered", clazz.getName()));
			}
		}

		try {
			converters.add(clazz.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ItargetConverter match(String target) {
		for (ItargetConverter converter : converters) {
			if (converter.match(target))
				return converter;
		}
		return null;
	}

}
