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


import com.jfinal.aop.Aop;
import io.jboot.utils.StrUtil;
import io.jpress.model.UserCart;
import io.jpress.model.UserOrderItem;

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

    private Map<String, ProductInfoQuerier> productInfoQuerierMap = new ConcurrentHashMap<>();
    private Map<String, ProductOptionsRender> productOptionsRenderMap = new ConcurrentHashMap<>();


    public Map<String, ProductInfoQuerier> getProductInfoQuerierMap() {
        return productInfoQuerierMap;
    }

    public void setProductInfoQuerierMap(Map<String, ProductInfoQuerier> productInfoQuerierMap) {
        this.productInfoQuerierMap = productInfoQuerierMap;
    }

    public void registerQuerier(String forProductType, ProductInfoQuerier querier) {
        Aop.inject(querier);
        productInfoQuerierMap.put(forProductType, querier);
    }

    public void unregisterQuerier(String forProductType) {
        productInfoQuerierMap.remove(forProductType);
    }

    public void registerProductOptionsRender(String forProductType, ProductOptionsRender render) {
        Aop.inject(render);
        productOptionsRenderMap.put(forProductType, render);
    }

    public void unregisterProductOptionsRender(String forProductType) {
        productOptionsRenderMap.remove(forProductType);
    }

    public Map<String, String> renderProductOptions(UserCart userCart) {
        if (userCart == null || StrUtil.isBlank(userCart.getProductType())) {
            return null;
        }
        ProductOptionsRender render = productOptionsRenderMap.get(userCart.getProductType());
        if (render == null) {
            return null;
        }
        return render.doRenderUserCartOptions(userCart);
    }

    public Map<String, String> renderProductOptions(UserOrderItem userOrderItem) {
        if (userOrderItem == null || StrUtil.isBlank(userOrderItem.getProductType())) {
            return null;
        }
        ProductOptionsRender render = productOptionsRenderMap.get(userOrderItem.getProductType());
        if (render == null) {
            return null;
        }
        return render.doRenderUserCartOptions(userOrderItem);
    }

    public BigDecimal queryDistAmount(UserOrderItem userOrderItem, String type, Object productId, Long payerUserId, Long distUserId) {
        if (StrUtil.isBlank(type)) {
            return BigDecimal.ZERO;
        }
        ProductInfoQuerier querier = productInfoQuerierMap.get(type);
        if (querier == null) {
            return BigDecimal.ZERO;
        }

        if (Objects.equals(payerUserId, distUserId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal distAmount = querier.queryDistAmount(userOrderItem, productId, payerUserId, distUserId);
        return distAmount == null || distAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : distAmount;
    }

    public boolean queryStatusNormal(String type, Object productId, Long buyerUserId) {
        if (StrUtil.isBlank(type)) {
            return true;
        }

        ProductInfoQuerier querier = productInfoQuerierMap.get(type);
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
        ProductInfoQuerier querier = productInfoQuerierMap.get(type);
        if (querier == null) {
            return null;
        }

        return querier.querySalePrice(productId, buyerUserId, distUserId);
    }


    public Long queryStockAmount(String type, Object productId) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        ProductInfoQuerier querier = productInfoQuerierMap.get(type);
        if (querier == null) {
            return null;
        }

        return querier.queryStockAmount(productId);
    }
}
