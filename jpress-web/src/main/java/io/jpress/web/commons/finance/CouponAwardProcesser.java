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
import io.jpress.core.finance.OrderStatusChangeListener;
import io.jpress.model.UserOrder;
import io.jpress.service.UserAmountStatementService;
import io.jpress.service.UserService;


/**
 * @author michael yang
 */
public class CouponAwardProcesser implements OrderStatusChangeListener {

    public static final Log LOG = Log.getLog(CouponAwardProcesser.class);

    @Inject
    private UserService userService;

    @Inject
    private UserAmountStatementService statementService;


    @Override
    public void onStatusChanged(UserOrder order) {


        if (order.isFinished() //交易结束，用户不能申请退款
                && StrUtil.isNotBlank(order.getCouponCode()) //优惠码不能为空
        ) {


        }
    }
}
