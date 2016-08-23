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
package io.jpress.menu;

import java.util.LinkedList;

import io.jpress.core.addon.HookInvoker;
import io.jpress.message.MessageKit;
import io.jpress.utils.StringUtils;

public class MenuManager {

	public static final String ACTION_INIT_MENU = "_INIT_MENU";

	static MenuManager manager = new MenuManager();
	static final LinkedList<MenuGroup> menuGroups = new LinkedList<MenuGroup>();

	public static MenuManager me() {
		return manager;
	}

	public String generateHtml() {
		if (menuGroups.isEmpty()) {
			MessageKit.sendMessage(ACTION_INIT_MENU, this);
		}

		HookInvoker.menuInitBefore(this);

		StringBuilder htmlBuilder = new StringBuilder();
		for (MenuGroup group : menuGroups) {
			htmlBuilder.append(group.generateHtml());
		}

		HookInvoker.menuInitAfter(this);

		return htmlBuilder.toString();
	}

	public void refresh() {
		menuGroups.clear();
	}

	public void addMenuGroup(MenuGroup gourp) {
		menuGroups.add(gourp);
	}

	public void addMenuGroup(int index, MenuGroup gourp) {
		menuGroups.add(index, gourp);
	}

	public void removeMenuGroupById(String id) {
		if (StringUtils.isBlank(id)) {
			return;
		}

		MenuGroup deleteGroup = null;
		for (MenuGroup menuGroup : menuGroups) {
			if (id.equals(menuGroup.getId())) {
				deleteGroup = menuGroup;
				break;
			}
		}
		if (deleteGroup != null) {
			menuGroups.remove(deleteGroup);
		}
	}

	public MenuGroup getMenuGroupById(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		for (MenuGroup menuGroup : menuGroups) {
			if (id.equals(menuGroup.getId())) {
				return menuGroup;
			}
		}
		return null;
	}

	public LinkedList<MenuGroup> getMenuGroups() {
		return menuGroups;
	}

}
