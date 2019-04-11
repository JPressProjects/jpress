/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.route;

import com.jfinal.core.Controller;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import io.jboot.Jboot;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.db.model.Columns;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleListener;
import io.jpress.module.route.sitemap.RouteSitemapProviderBuilder;

import java.util.List;

/**
 * @author Eric.Huang （ninemm@126.com）
 * @version V1.0
 * @Title: Module 监听器
 * @Description: 每个 module 都应该有这样的一个监听器，用来配置自身Module的信息，比如后台菜单等
 * @Package io.jpress.module.route
 */
public class ToursModuleListener extends JbootAppListenerBase implements ModuleListener {


    @Override
    public String onRenderDashboardBox(Controller controller) {
        return null;
    }

    @Override
    public String onRenderToolsBox(Controller controller) {
        return null;
    }

    @Override
    public void onConfigAdminMenu(List<MenuGroup> adminMenus) {
		MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId("tours");
        menuGroup.setText("旅游");
        menuGroup.setIcon("<i class=\"fa fa-cloud\"></i>");
        menuGroup.setOrder(1);
        adminMenus.add(menuGroup);
    }

    @Override
    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus) {

    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {
        plugins.add(new Cron4jPlugin());
    }

    @Override
    public void onStart() {
        RouteSitemapProviderBuilder.me().init();
    }
}