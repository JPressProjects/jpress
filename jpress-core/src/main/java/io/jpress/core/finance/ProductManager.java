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
package io.jpress.core.finance;


import com.jfinal.log.Log;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProductManager {

    private static final Log LOG = Log.getLog(ProductManager.class);

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

    public void registerQuerier(String table, ProductInfoQuerier querier) {
        queriers.put(table, querier);
    }


    public BigDecimal queryDistAmount(String tableName, Object productId, Long payerUserId, Long distUserId) {
        ProductInfoQuerier getter = queriers.get(tableName);
        if (getter == null) {
            return BigDecimal.ZERO;
        }

        if (Objects.equals(payerUserId, distUserId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal distAmount = getter.queryDistAmount(productId, payerUserId, distUserId);
        return distAmount == null || distAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : distAmount;
    }
}
