/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.product;

import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import com.jfinal.template.Engine;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleListener;
import io.jpress.core.finance.ProductManager;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 产品模块初始化
 * @Package io.jpress
 */
public class ProductModuleInitializer extends JbootAppListenerBase implements ModuleListener {


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
        menuGroup.setId("product");
        menuGroup.setText("商品");
        menuGroup.setIcon("<i class=\"fa fa-fw fa-tags\"></i>");
        menuGroup.setOrder(99);
        adminMenus.add(menuGroup);
    }

    @Override
    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus) {


    }


    @Override
    public void onEngineConfig(Engine engine) {
        engine.addSharedFunction("/WEB-INF/views/commons/product/defaultProductCommentPage.html");
        engine.addSharedFunction("/WEB-INF/views/commons/product/defaultProductHeader.html");
    }


    @Override
    public void onStart() {
        ProductManager.me().registerQuerier("product", Aop.get(JPressProductInfoQuerier.class));
    }
}
