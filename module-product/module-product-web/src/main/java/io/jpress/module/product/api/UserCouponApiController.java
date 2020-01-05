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
package io.jpress.module.product.api;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.CouponCode;
import io.jpress.model.User;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponService;
import io.jpress.web.base.ApiControllerBase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping("/api/usercoupon")
public class UserCouponApiController extends ApiControllerBase {

    @Inject
    private CouponCodeService couponCodeService;
    /**
     * 用户的优惠券列表
     */
    public void index(){
        List<CouponCode> renderList = couponCodeService.findAvailableByUserId(getLoginedUser().getId());
        renderOkDataJson(renderList);
    }


    /**
     * 支付是，获取用户可用的优惠券
     */
    public void findAvailable(){
        String price = getPara("price");
        List<CouponCode> couponCodes = couponCodeService.findAvailableByUserId(getLoginedUser().getId(), new BigDecimal(price));
        renderOkDataJson(couponCodes);
    }



}
