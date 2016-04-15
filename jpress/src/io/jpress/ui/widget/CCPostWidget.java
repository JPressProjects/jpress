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
package io.jpress.ui.widget;

import io.jpress.core.ui.JWidget;

public class CCPostWidget extends JWidget {

	public CCPostWidget() {
		super();
		put("aa1", "bb1");
		put("aa2", "bb2");
		put("aa3", "bb3");
		put("aa4", "bb4");
	}

	@Override
	public String onRenderHtmlForSetting(WidgetConfig config) {

		return null;
	}

	@Override
	public String onRenderHtml(WidgetConfig config) {

		return null;
	}

}
