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
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.service.RoleService;
import io.jpress.web.base.AdminControllerBase;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/finance", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FinanceController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @AdminMenu(text = "订单管理", groupId = JPressConsts.SYSTEM_MENU_FINANCE, order = 0)
    public void index() {
        render("order/list.html");

    }

    @AdminMenu(text = "设置", groupId = JPressConsts.SYSTEM_MENU_FINANCE)
    public void my() {
        render("my.html");
    }

}
