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
import io.jpress.core.payment.PaymentSuccessListener;
import io.jpress.model.PaymentRecord;
import io.jpress.model.UserAmountStatement;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;

import java.math.BigDecimal;


public class RechargePaymentSuccessListener implements PaymentSuccessListener {

    public static final Log LOG = Log.getLog(RechargePaymentSuccessListener.class);


    @Override
    public void onSuccess(PaymentRecord payment) {

        if (PaymentRecord.TRX_TYPE_RECHARGE.equals(payment.getTrxType()) && payment.isPaySuccess()) {

            boolean updateSucess = Db.tx(() -> {

                UserService userService = Aop.get(UserService.class);

                BigDecimal userAmount = userService.queryUserAmount(payment.getPayerUserId());
                userService.updateUserAmount(payment.getPayerUserId(), userAmount, payment.getPayAmount());

                UserAmountStatement statement = new UserAmountStatement();
                statement.setUserId(payment.getPayerUserId());
                statement.setAction(UserAmountStatement.ACTION_RECHARGE);
                statement.setActionDesc(UserAmountStatement.ACTION_RECHARGE_DESC);
                statement.setActionName("充值");
                statement.setActionRelativeType("payment_record");
                statement.setActionRelativeId(payment.getId());

                statement.setOldAmount(userAmount);
                statement.setChangeAmount(payment.getPayAmount());
                statement.setNewAmount(userAmount.subtract(payment.getPayAmount()));

                if (userService.updateUserAmount(payment.getPayerUserId(), userAmount, payment.getPayAmount())){
                    return false;
                }

                UserAmountStatementService statementService = Aop.get(UserAmountStatementService.class);
                if (statementService.save(statement) == null){
                    return false;
                }

                return true;
            });

            if (!updateSucess) {
                LOG.error("update user amount fail in recharge success。");
            }

        }

    }
}
