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
package io.jpress.core.payment;


import com.jfinal.log.Log;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistManager {

    private static final Log LOG = Log.getLog(DistManager.class);

    public static final DistManager me = new DistManager();

    private DistManager() {
    }

    public static final DistManager me() {
        return me;
    }

    public Map<String, DistAmountGetter> amountGetters = new ConcurrentHashMap<>();

    public Map<String, DistAmountGetter> getAmountGetters() {
        return amountGetters;
    }

    public void setAmountGetters(Map<String, DistAmountGetter> amountGetters) {
        this.amountGetters = amountGetters;
    }

    public void registerDistAmountGetter(String table, DistAmountGetter getter) {
        amountGetters.put(table, getter);
    }

    public BigDecimal getAmount(String tableName, Object productId, Long payerUserId, Long distUserId) {
        DistAmountGetter getter = amountGetters.get(tableName);
        if (getter == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal distAmount = getter.onGetDistAmount(productId, payerUserId, distUserId);
        return distAmount == null || distAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : distAmount;
    }
}
