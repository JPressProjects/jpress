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

import io.jpress.model.UserOrderItem;

import java.math.BigDecimal;


public interface ProductInfoQuerier {

    /**
     * 查询该商品的分销价格
     *
     *
     * @param userOrderItem
     * @param productId
     * @param buyerUserId
     * @param distUserId
     * @return
     */
    public BigDecimal queryDistAmount(UserOrderItem userOrderItem, Object productId, Long buyerUserId, Long distUserId);


    /**
     * 查询该商品的销售价格
     *
     * @param productId
     * @param buyerUserId
     * @param distUserId
     * @return
     */
    public BigDecimal querySalePrice(Object productId, Long buyerUserId, Long distUserId);


    /**
     * 查询该商品是否正常销售
     *
     * @param productId
     * @param buyerUserId
     * @return return true if product can be pruchased
     */
    public boolean queryStatusNormal(Object productId, Long buyerUserId);


    /**
     * 查询商品库存数量
     * @param productId
     * @return
     */
    public Long queryStockAmount(Object productId);

}
