/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.template;

import io.jboot.Jboot;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;
import io.jpress.web.interceptor.TemplateInterceptor;

import java.util.List;

public class TemplateMenuManager {


    private static final TemplateMenuManager me = new TemplateMenuManager();

    private TemplateMenuManager() {

    }


    public static TemplateMenuManager me() {
        return me;
    }

    public void init() {
        MenuService menuService = Jboot.bean(MenuService.class);
        List<Menu> menus = menuService.findListByType(Menu.TYPE_MAIN);
        TemplateInterceptor.setMenus(menus);
    }


}
