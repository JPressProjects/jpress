/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.core.module;

import com.jfinal.core.Controller;
import io.jpress.core.menu.MenuGroup;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 监听器
 * @Package io.jpress
 */
public interface ModuleListener {

    /**
     * 对后台的面板进行渲染
     * <p>
     * 返回的内容是 html 文件地址，html 文件应该只包含 单个 div ，这个 div 会被嵌套在 面板里
     * <p>
     * 暂时不支持顺序
     *
     * @param controller
     * @return 返回要 html 文件地址
     */
    default String onRenderDashboardBox(Controller controller) {
        return null;
    }


    /**
     * 对后台的 "工具箱" 进行进行渲染
     *
     * @param controller
     * @return
     */
    default String onRenderToolsBox(Controller controller) {
        return null;
    }


    /**
     * 配置后台的菜单
     *
     * @param adminMenus
     */
    default void onConfigAdminMenu(List<MenuGroup> adminMenus){};


}
