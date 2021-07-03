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
package io.jpress.module.product.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.model.CouponCode;
import io.jpress.service.CouponCodeService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping("/api/userCoupon")
@Api("优惠券相关API")
public class UserCouponApiController extends ApiControllerBase {

    @Inject
    private CouponCodeService couponCodeService;

    @ApiOper("查询可用的优惠券")
    public Ret listByUserId(@ApiPara("用户ID") @NotNull Long userId) {
        List<CouponCode> renderList = couponCodeService.findAvailableByUserId(userId);
        return Ret.ok().set("list", renderList);
    }


    @ApiOper("查询可用的优惠券")
    public Ret listByUserIdAndPrice(@ApiPara("用户ID") @NotNull Long userId, @ApiPara("消费的订单价格") @NotNull BigDecimal price) {
        List<CouponCode> couponCodes = couponCodeService.findAvailableByUserId(userId, price);
        return Ret.ok().set("list", couponCodes);
    }


    @ApiOper("删除优惠码")
    public Ret doDelete(@ApiPara("优惠码ID") Long id) {
        couponCodeService.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "创建新的优惠码", contentType = ContentType.JSON)
    public Ret doCreate(@ApiPara("优惠码 JSON 信息") @JsonBody CouponCode couponCode) {
        Object id = couponCodeService.save(couponCode);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "改变优惠码信息", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("优惠码 JSON 信息") CouponCode couponCode) {
        couponCodeService.update(couponCode);
        return Rets.OK;
    }


}
