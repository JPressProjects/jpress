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
package io.jpress.web.commons.pay;

import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import io.jpress.commons.pay.PayStatus;
import io.jpress.core.finance.PaymentSuccessListener;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountStatement;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;
import io.jpress.service.UserService;

import java.math.BigDecimal;
import java.util.List;


public class OrderPaymentSuccessListener implements PaymentSuccessListener {

    public static final Log LOG = Log.getLog(OrderPaymentSuccessListener.class);


    @Override
    public void onSuccess(PaymentRecord payment) {

        if (PaymentRecord.TRX_TYPE_ORDER.equals(payment.getTrxType())) {

            boolean updateSucess = Db.tx(() -> {

                UserOrderService orderService = Aop.get(UserOrderService.class);
                UserOrder userOrder = orderService.findByPaymentId(payment.getId());

                userOrder.setPayStatus(PayStatus.getSuccessIntStatusByType(payment.getPayType()));
                userOrder.setTradeStatus(UserOrder.TRADE_STATUS_COMPLETED);

                if (!orderService.update(userOrder)) {
                    return false;
                }


                UserOrderItemService itemService = Aop.get(UserOrderItemService.class);
                List<UserOrderItem> userOrderItems = itemService.findListByOrderId(userOrder.getId());
                for (UserOrderItem item : userOrderItems) {
                    Boolean isVirtual = item.getProductVirtual();
                    if (isVirtual != null && isVirtual) {
                        item.setStatus(UserOrderItem.STATUS_FINISHED);//交易结束
                    } else {
                        item.setStatus(UserOrderItem.STATUS_COMPLETED);//交易完成
                    }
                    if (!itemService.update(item)) {
                        return false;
                    }

                    distSettler(item, payment);
                }
                return true;
            });

            if (!updateSucess) {
                LOG.error("update order fail or update orderItem fail in pay success。");
            }

        }

    }

    /**
     * 处理分销情况
     *
     * @param orderItem
     * @param payment
     */
    private void distSettler(UserOrderItem orderItem, PaymentRecord payment) {

        BigDecimal distAmount = orderItem.getDistAmount().multiply(BigDecimal.valueOf(orderItem.getProductCount()));

        if (orderItem.isFinished() //交易结束，用户不能申请退款
                && orderItem.getDistUserId() != null //分销用户不能为空
                && orderItem.getBuyerId() != null  //支付用户不能为空
                && orderItem.getDistUserId().equals(orderItem.getBuyerId()) //分销用户和支付用户不能是同一个人
                && orderItem.getPayAmount() != null //支付金额不能为空
                && orderItem.getPayAmount().compareTo(BigDecimal.ZERO) > 0 //支付金额必须大于0
                && orderItem.getDistAmount() != null //分销金额不能为空
                && orderItem.getPayAmount().compareTo(distAmount) > 0 //支付金额必须大于分销金额
        ) {


            UserService userService = Aop.get(UserService.class);
            UserAmountStatementService statementService = Aop.get(UserAmountStatementService.class);

            BigDecimal userAmount = userService.queryUserAmount(orderItem.getDistUserId());

            //更新用于余额
            if (userService.updateUserAmount(orderItem.getDistUserId(), userAmount,
                    distAmount)) {

                UserAmountStatement statement = new UserAmountStatement();
                statement.setUserId(orderItem.getDistUserId());
                statement.setActionDesc(UserAmountStatement.ACTION_DIST);
                statement.setActionName("分销收入");
                statement.setActionDesc("分销收入");
                statement.setActionRelativeType("user_order_item");
                statement.setActionRelativeId(orderItem.getId());
                statement.setActionPaymentId(payment.getId());
                statement.setOldAmount(userAmount);
                statement.setNewAmount(userAmount.add(distAmount));
                statement.setChangeAmount(distAmount);

                statementService.save(statement);
            }
        }
    }
}
