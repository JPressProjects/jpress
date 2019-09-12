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

import com.jfinal.log.Log;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.web.base.AdminControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/order", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _OrderController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_OrderController.class);


    @AdminMenu(text = "概况", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 0)
    public void index() {
        renderText("概况");
    }


    @AdminMenu(text = "订单", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 1)
    public void list() {
        renderText("概况");
    }


    @AdminMenu(text = "优惠券", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 2)
    public void coupon() {
        renderText("概况");
    }

    @AdminMenu(text = "设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 3)
    public void setting() {
        renderText("概况");
    }


}
