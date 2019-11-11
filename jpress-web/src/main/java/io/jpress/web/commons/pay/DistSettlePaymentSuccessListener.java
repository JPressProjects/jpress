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
import io.jpress.core.payment.PaymentSuccessListener;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountStatement;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;

import java.math.BigDecimal;


public class DistSettlePaymentSuccessListener implements PaymentSuccessListener {

    public static final Log LOG = Log.getLog(DistSettlePaymentSuccessListener.class);


    @Override
    public void onSuccess(PaymentRecord payment) {

        if (payment.getDistUserId() != null //分销用户不能为空
                && payment.getPayerUserId() != null  //支付用户不能为空
                && payment.getDistUserId().equals(payment.getPayerUserId()) //分销用户和支付用户不能是同一个人
                && payment.getPaySuccessAmount() != null //支付金额不能为空
                && payment.getPaySuccessAmount().compareTo(BigDecimal.ZERO) > 0 //支付金额必须大于0
                && payment.getDistAmount() != null //分销金额不能为空
                && payment.getPaySuccessAmount().compareTo(payment.getDistAmount()) > 0 //支付金额必须大于分销金额
        ) {

            UserService userService = Aop.get(UserService.class);
            UserAmountStatementService statementService = Aop.get(UserAmountStatementService.class);

            BigDecimal userAmount = userService.queryUserAmount(payment.getDistUserId());

            //更新用于余额
            if (userService.updateUserAmount(payment.getDistUserId(), userAmount,
                    payment.getDistAmount())) {

                UserAmountStatement statement = new UserAmountStatement();
                statement.setUserId(payment.getDistUserId());
                statement.setActionDesc(UserAmountStatement.ACTION_DIST);
                statement.setActionName("分销收入");
                statement.setActionDesc("分销收入");
                statement.setActionRelativeType("payment_record");
                statement.setActionRelativeId(payment.getId());
                statement.setActionPaymentId(payment.getId());
                statement.setOldAmount(userAmount);
                statement.setNewAmount(userAmount.add(payment.getDistAmount()));
                statement.setChangeAmount(payment.getDistAmount());

                statementService.save(statement);
            }
        }
    }
}
