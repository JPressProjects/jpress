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
package io.jpress.core.finance.render;

import com.jfinal.core.Controller;
import io.jpress.model.UserOrder;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/2/1
 */
public class DefaultAdminOrderDetailPageRender implements AdminOrderDetailPageRender {

    public static DefaultAdminOrderDetailPageRender me = new DefaultAdminOrderDetailPageRender();

    @Override
    public void doRender(Controller controller, UserOrder userOrder) {
        controller.render("order/order_detail.html");
    }
}
