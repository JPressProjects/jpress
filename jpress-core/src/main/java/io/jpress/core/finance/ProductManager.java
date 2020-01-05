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


import io.jboot.utils.StrUtil;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProductManager {

    private static final ProductManager me = new ProductManager();

    private ProductManager() {
    }

    public static final ProductManager me() {
        return me;
    }

    private Map<String, ProductInfoQuerier> queriers = new ConcurrentHashMap<>();


    public Map<String, ProductInfoQuerier> getQueriers() {
        return queriers;
    }

    public void setQueriers(Map<String, ProductInfoQuerier> queriers) {
        this.queriers = queriers;
    }

    public void registerQuerier(String type, ProductInfoQuerier querier) {
        queriers.put(type, querier);
    }


    public BigDecimal queryDistAmount(String type, Object productId, Long payerUserId, Long distUserId) {
        if (StrUtil.isBlank(type)) {
            return BigDecimal.ZERO;
        }
        ProductInfoQuerier querier = queriers.get(type);
        if (querier == null) {
            return BigDecimal.ZERO;
        }

        if (Objects.equals(payerUserId, distUserId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal distAmount = querier.queryDistAmount(productId, payerUserId, distUserId);
        return distAmount == null || distAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : distAmount;
    }

    public boolean queryStatusNormal(String type, Object productId, Long buyerUserId) {
        if (StrUtil.isBlank(type)) {
            return true;
        }

        ProductInfoQuerier querier = queriers.get(type);
        //没有注册 querier，说明该商品任何时候都可以被购买
        if (querier == null) {
            return true;
        }

        return querier.queryStatusNormal(productId, buyerUserId);
    }


    public BigDecimal querySalePrice(String type, Object productId, Long buyerUserId, Long distUserId) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        ProductInfoQuerier querier = queriers.get(type);
        if (querier == null) {
            return null;
        }

        return querier.querySalePrice(productId, buyerUserId, distUserId);
    }


    public Long queryStockAmount(String type, Object productId) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        ProductInfoQuerier querier = queriers.get(type);
        if (querier == null) {
            return null;
        }

        return querier.queryStockAmount(productId);
    }
}
