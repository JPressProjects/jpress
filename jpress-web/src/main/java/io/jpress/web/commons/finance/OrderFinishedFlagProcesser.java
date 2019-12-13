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
package io.jpress.web.commons.finance;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import io.jpress.core.finance.OrderItemStatusChangeListener;
import io.jpress.core.finance.OrderManager;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;

import java.util.List;


/**
 * @author michael yang
 * 用于对 UserOrder 的 完成状态 进行设置
 */
public class OrderFinishedFlagProcesser implements OrderItemStatusChangeListener {

    public static final Log LOG = Log.getLog(OrderFinishedFlagProcesser.class);

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserOrderItemService orderItemService;


    @Override
    public void onStatusChanged(UserOrderItem orderItem) {

        if (orderItem.isFinished()) {

            UserOrder order = orderService.findById(orderItem.getOrderId());
            if (order == null) {
                return;
            }

            if (order.isFinished()) {
                return;
            }

            List<UserOrderItem> userOrderItems = orderItemService.findListByOrderId(order.getId());
            for (UserOrderItem item : userOrderItems) {
                if (!item.isFinished()){
                    return;
                }
            }

            order.setTradeStatus(UserOrder.TRADE_STATUS_FINISHED);
            orderService.update(order);

            OrderManager.me().notifyOrderStatusChanged(order);
        }
    }
}
