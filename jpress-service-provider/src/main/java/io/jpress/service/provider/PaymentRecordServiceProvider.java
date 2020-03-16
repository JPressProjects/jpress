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
package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.CacheUtil;
import io.jboot.utils.StrUtil;
import io.jpress.commons.pay.PayStatus;
import io.jpress.core.finance.PaymentManager;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

@Bean
public class PaymentRecordServiceProvider extends JbootServiceBase<PaymentRecord> implements PaymentRecordService {

    @Override
    public PaymentRecord findByTrxNo(String trxno) {
        return StrUtil.isBlank(trxno) ? null : DAO.findFirstByColumn(Column.create("trx_no", trxno));
    }

    @Override
    public Page<PaymentRecord> paginate(int page, int pagesize, Columns columns) {
        return DAO.paginateByColumns(page, pagesize, columns, "id desc");
    }

    @Override
    public PaymentRecord queryCacheByTrxno(String trx) {
        return CacheUtil.get("payment_trx", trx);
    }

    @Override
    public void notifySuccess(long paymentId) {
        PaymentRecord payment = findById(paymentId);
        PaymentManager.me().notifySuccess(payment);
        CacheUtil.put("payment_trx", payment.getTrxNo(), payment, 60 * 60);
    }

    @Override
    public int queryMonthAmount() {
        return Db.queryInt("select coalesce(sum(pay_success_amount),0) from payment_record where created > ? and pay_status >= ?",
                DateUtils.truncate(new Date(), Calendar.MONTH), PayStatus.SUCCESS_ALIPAY.getStatus());
    }
}