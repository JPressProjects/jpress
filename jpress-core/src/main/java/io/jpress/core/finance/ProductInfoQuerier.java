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
package io.jpress.core.finance;

import io.jpress.model.UserCart;

import java.math.BigDecimal;


public interface ProductInfoQuerier {

    /**
     * 查询产品的销售分成金额
     *
     * @param userCart
     * @param payerId
     * @return
     */
    public BigDecimal queryDistAmount(UserCart userCart, Long productId, String productSpec, Long payerId, Long distUserId);


    /**
     * 查询该商品的销售价格
     *
     * @param userCart
     * @param productId
     * @param productSpec
     * @param payerId
     * @return
     */
    public BigDecimal querySalePrice(UserCart userCart, Long productId, String productSpec, Long payerId);


    /**
     * 查询该商品是否正常销售
     *
     * @param userCart
     * @param productId
     * @param productSpec
     * @param payerId
     * @return
     */
    public boolean queryStatusNormal(UserCart userCart, Long productId, String productSpec, Long payerId);


    /**
     * 查询商品库存数量
     *
     * @param userCart
     * @param productId
     * @param productSpec
     * @return
     */
    public Long queryStockAmount(UserCart userCart, Long productId, String productSpec);

}
