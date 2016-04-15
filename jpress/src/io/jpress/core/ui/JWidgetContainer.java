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
package io.jpress.core.ui;

import io.jpress.core.ui.JWidget.WidgetStorage;
import io.jpress.model.Option;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class JWidgetContainer extends JTag {

	LinkedList<JWidget> widgetList;

	@Override
	public void onRender() {

		initWidgetList();

		StringBuilder htmlBuilder = new StringBuilder();
		if (widgetList != null) {
			for (JWidget widget : widgetList) {
				htmlBuilder.append(widget.getFrontEndHtml());
			}
		}

		renderText(htmlBuilder.toString());
	}

	private void initWidgetList() {
		String name = getParam("name");
		String value = Option.cacheValue("widget_" + name);
		if (value == null) {
			List<WidgetStorage> wsList = JSON.parseArray(value, WidgetStorage.class);
			if (wsList != null && wsList.size() > 0) {
				widgetList = new LinkedList<JWidget>();
				for (WidgetStorage ws : wsList) {
					widgetList.add(ws.getWidget());
				}
			}
		}
	}

}
