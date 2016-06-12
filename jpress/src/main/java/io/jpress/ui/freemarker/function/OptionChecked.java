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
package io.jpress.ui.freemarker.function;

import io.jpress.core.render.freemarker.JFunction;
import io.jpress.model.Option;
import io.jpress.utils.StringUtils;

public class OptionChecked extends JFunction {

	@Override
	public Object onExec() {
		String key = getToString(0);
		if (key == null)
			return "";

		String value = getToString(1);
		if (StringUtils.isNotBlank(value)) {
			String setting = Option.findValue(key);
			if (value.equals(setting)) {
				return "checked=\"checked\"";
			} else {
				return "";
			}
		}

		if (key.startsWith("!")) {
			Boolean bool = tryToGetBool(key.substring(1));
			if (bool != null && !bool) {
				return "checked=\"checked\"";
			}
		} else {
			Boolean bool = tryToGetBool(key);
			if (bool != null && bool) {
				return "checked=\"checked\"";
			}
		}

		return "";
	}

	private Boolean tryToGetBool(String key) {
		String value = Option.findValue(key);
		Boolean ret = null;
		try {
			ret = Boolean.parseBoolean(value);
		} catch (Exception e) {
		}

		return ret;
	}

}
