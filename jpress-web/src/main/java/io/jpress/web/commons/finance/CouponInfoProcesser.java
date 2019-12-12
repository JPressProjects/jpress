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
import io.jboot.utils.StrUtil;
import io.jpress.core.finance.PaymentSuccessListener;
import io.jpress.model.*;
import io.jpress.service.*;


/**
 * @author michael yang
 * <p>
 * 用于生成优惠码的使用记录
 */
public class CouponInfoProcesser implements PaymentSuccessListener {

    public static final Log LOG = Log.getLog(CouponInfoProcesser.class);

    @Inject
    private CouponService couponService;

    @Inject
    private CouponCodeService couponCodeService;

    @Inject
    private UserOrderService orderService;

    @Inject
    private UserService userService;

    @Inject
    private CouponUsedRecordService curService;


    @Override
    public void onSuccess(PaymentRecord payment) {

        if (PaymentRecord.TRX_TYPE_ORDER.equals(payment.getTrxType())) {

            UserOrder userOrder = orderService.findByPaymentId(payment.getId());
            String couponCodeStr = userOrder.getCouponCode();
            if (StrUtil.isBlank(couponCodeStr)) {
                return;
            }

            CouponCode couponCode = couponCodeService.findByCode(couponCodeStr);
            //可能已经被删除
            if (couponCode == null) {
                return;
            }

            Coupon coupon = couponService.findById(couponCode.getCouponId());
            if (coupon == null) {
                return;
            }

            // 标识该优惠码已经被使用
            Boolean withMulti = coupon.getWithMulti();
            if (withMulti != null && !withMulti){
                couponCode.setStatus(CouponCode.STATUS_USED);
                couponCodeService.update(couponCode);
            }

            User usedUser = userService.findById(userOrder.getBuyerId());
            User codeUser = userService.findById(couponCode.getUserId());

            if (usedUser == null || codeUser == null){
                return;
            }


            CouponUsedRecord cur = new CouponUsedRecord();
            cur.setUsedUserId(usedUser.getId());
            cur.setUsedUserNickname(usedUser.getNickname());
            cur.setUsedOrderId(userOrder.getId());
            cur.setUserPaymentId(payment.getId());
            cur.setCodeId(couponCode.getId());
            cur.setCode(couponCodeStr);
            cur.setCodeUserId(codeUser.getId());
            cur.setCodeUserNickname(codeUser.getNickname());
            cur.setCouponId(coupon.getId());

            curService.save(cur);

            couponService.doSyncUsedCount(coupon.getId());
        }
    }
}
