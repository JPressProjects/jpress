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
        productInfoQuerierMap.put(forProductType, Aop.inject(querier));
    }

    public void unregisterQuerier(String forProductType) {
        productInfoQuerierMap.remove(forProductType);
    }

    public void registerOptionsRender(String forProductType, ProductOptionsRender render) {
        productOptionsRenderMap.put(forProductType, Aop.inject(render));
    }

    public void unregisterOptionsRender(String forProductType) {
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

    public Map renderProductOptions(UserOrderItem userOrderItem) {
        if (userOrderItem == null || StrUtil.isBlank(userOrderItem.getProductType())) {
            return null;
        }
        ProductOptionsRender render = productOptionsRenderMap.get(userOrderItem.getProductType());
        if (render == null) {
            return null;
        }
        return render.doRenderUserCartOptions(userOrderItem);
    }

    /**
     * 查询产品的分销金额
     *
     * @param userCart
     * @param payerId
     * @param distUserId
     * @return
     */
    public BigDecimal queryDistAmount(UserCart userCart, Long productId, String productSpec, Long payerId, Long distUserId) {
        if (userCart == null || StrUtil.isBlank(userCart.getProductType())) {
            return BigDecimal.ZERO;
        }
        ProductInfoQuerier querier = productInfoQuerierMap.get(userCart.getProductType());
        if (querier == null) {
            return BigDecimal.ZERO;
        }

        if (Objects.equals(distUserId, payerId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal distAmount = querier.queryDistAmount(userCart, productId, productSpec, payerId, distUserId);
        return distAmount == null || distAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : distAmount;
    }


    /**
     * 查询产品
     *
     * @param userCart
     * @param productId
     * @param productSpec
     * @param payerId
     * @return
     */
    public boolean queryStatusNormal(UserCart userCart, Long productId, String productSpec, Long payerId) {
        if (userCart == null || StrUtil.isBlank(userCart.getProductType())) {
            return true;
        }

        ProductInfoQuerier querier = productInfoQuerierMap.get(userCart.getProductType());
        //没有注册 querier，说明该商品任何时候都可以被购买
        if (querier == null) {
            return true;
        }

        return querier.queryStatusNormal(userCart, productId, productSpec, payerId);
    }


    /**
     * 查询产品的价格，当一个商品被添加到购物车后，可能还会变动价格（或者有会员价等）
     *
     * @param userCart
     * @param productId
     * @param productSpec
     * @param payerId
     * @return
     */
    public BigDecimal querySalePrice(UserCart userCart, Long productId, String productSpec, Long payerId) {
        if (userCart == null || StrUtil.isBlank(userCart.getProductType())) {
            return null;
        }
        ProductInfoQuerier querier = productInfoQuerierMap.get(userCart.getProductType());
        if (querier == null) {
            return null;
        }

        return querier.querySalePrice(userCart, productId, productSpec, payerId);
    }


    /**
     * 查询产品的库存，当库存不足的时候不让购买
     *
     * @param userCart
     * @param productId
     * @param productSpec
     * @return
     */
    public Long queryStockAmount(UserCart userCart, Long productId, String productSpec) {
        if (userCart == null || StrUtil.isBlank(userCart.getProductType())) {
            return null;
        }
        ProductInfoQuerier querier = productInfoQuerierMap.get(userCart.getProductType());
        if (querier == null) {
            return null;
        }

        return querier.queryStockAmount(userCart, productId, productSpec);
    }
}
