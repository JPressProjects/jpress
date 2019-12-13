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
import com.jfinal.plugin.activerecord.Db;
import io.jpress.commons.pay.PayStatus;
import io.jpress.core.finance.OrderManager;
import io.jpress.core.finance.PaymentSuccessListener;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;

import java.util.List;


public class OrderPaymentSuccessListener implements PaymentSuccessListener {

    public static final Log LOG = Log.getLog(OrderPaymentSuccessListener.class);

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserOrderItemService itemService;

    @Override
    public void onSuccess(PaymentRecord payment) {

        if (PaymentRecord.TRX_TYPE_ORDER.equals(payment.getTrxType())) {

            boolean updateSucess = Db.tx(() -> {

                UserOrder userOrder = orderService.findByPaymentId(payment.getId());

                userOrder.setPayStatus(PayStatus.getSuccessIntStatusByType(payment.getPayType()));
                userOrder.setTradeStatus(UserOrder.TRADE_STATUS_COMPLETED);

                if (!orderService.update(userOrder)) {
                    return false;
                }


                List<UserOrderItem> userOrderItems = itemService.findListByOrderId(userOrder.getId());
                for (UserOrderItem item : userOrderItems) {
                    if (item.isVirtualProduct()) {
                        item.setStatus(UserOrderItem.STATUS_FINISHED);//交易结束
                    } else {
                        item.setStatus(UserOrderItem.STATUS_COMPLETED);//交易完成
                    }
                    if (!itemService.update(item)) {
                        return false;
                    }
                    OrderManager.me().notifyItemStatusChanged(item);
                }

                OrderManager.me().notifyOrderStatusChanged(userOrder);
                return true;
            });

            if (!updateSucess) {
                LOG.error("update order fail or update orderItem fail in pay success。");
            }
        }
    }

}
