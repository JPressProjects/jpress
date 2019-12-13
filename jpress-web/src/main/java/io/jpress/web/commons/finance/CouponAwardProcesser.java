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
import io.jboot.utils.StrUtil;
import io.jpress.core.finance.OrderStatusChangeListener;
import io.jpress.model.*;
import io.jpress.service.*;

import java.math.BigDecimal;


/**
 * @author michael yang
 */
public class CouponAwardProcesser implements OrderStatusChangeListener {

    public static final Log LOG = Log.getLog(CouponAwardProcesser.class);

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

    @Inject
    private UserAmountStatementService statementService;


    @Override
    public void onStatusChanged(UserOrder order) {


        if (order.isFinished() //交易结束，用户不能申请退款
                && StrUtil.isNotBlank(order.getCouponCode()) //优惠码不能为空
        ) {

            String couponCodeStr = order.getCouponCode();
            if (StrUtil.isBlank(couponCodeStr)) {
                return;
            }

            CouponCode couponCode = couponCodeService.findByCode(couponCodeStr);
            // 优惠码已经被删除 或者 已经被冻结
            if (couponCode == null || !couponCode.isNormal()) {
                return;
            }

            Coupon coupon = couponService.findById(couponCode.getCouponId());
            // 优惠券已经删除 或者 不可用
            if (coupon == null || !coupon.isNormal()) {
                return;
            }

            // 不是分销的优惠券
            Boolean withAward = coupon.getWithAward();
            if (withAward == null || !withAward){
                return;
            }

            // 自己消费自己的优惠券 不会获得奖励
            if (couponCode.getUserId().equals(order.getBuyerId())){
                return;
            }

            // 订单总金额
            BigDecimal orderTotalAmount = order.getOrderTotalAmount();
            if (orderTotalAmount == null || orderTotalAmount.compareTo(BigDecimal.ZERO) < 0){
                return;
            }

            // 优惠券金额
            BigDecimal couponAmount = order.getCouponAmount();
            if (couponAmount == null || couponAmount.compareTo(BigDecimal.ZERO) < 0){
                return;
            }

            // 奖励金额
            BigDecimal awardAmount = coupon.getAwardAmount();
            if (awardAmount == null || awardAmount.compareTo(BigDecimal.ZERO) < 0){
                return;
            }

            // 订单总金额 应该大于 优惠券金额 + 奖励金额。（否则就亏本）
            // 此处不考虑分销情况，如果有分销，可能会亏本
            if (orderTotalAmount.compareTo(couponAmount.add(awardAmount))<= 0){
                return;
            }

            User awardUser = userService.findById(couponCode.getUserId());
            if (awardUser == null || !awardUser.isStatusOk()){
                return;
            }


            boolean awardSucess = Db.tx(() -> {

                // 流水检查
                UserAmountStatement existStatement = statementService
                        .findOneByUserIdAndRelative(awardUser.getId(), "user_order", order.getId());
                if (existStatement != null) {
                    return false;
                }


                BigDecimal userAmount = userService.queryUserAmount(awardUser.getId());

                //更新余额
                if (!userService.updateUserAmount(awardUser.getId(), userAmount,
                        awardAmount)) {
                    return false;
                }

                // 生成流水
                UserAmountStatement statement = new UserAmountStatement();
                statement.setUserId(awardUser.getId());
                statement.setActionDesc(UserAmountStatement.ACTION_COUPON);
                statement.setActionName("优惠码分销收入");
                statement.setActionDesc("优惠码分销收入");
                statement.setActionRelativeType("user_order");
                statement.setActionRelativeId(order.getId());
                statement.setOldAmount(userAmount);
                statement.setNewAmount(userAmount.add(awardAmount));
                statement.setChangeAmount(awardAmount);

                if (statementService.save(statement) == null) {
                    return false;
                }

                return true;
            });



            if (!awardSucess) {
                LOG.error("coupon award fail !!!");
            }

        }
    }
}
