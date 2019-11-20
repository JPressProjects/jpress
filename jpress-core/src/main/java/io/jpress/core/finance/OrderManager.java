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
import io.jpress.model.UserOrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderManager {

    private static final Log LOG = Log.getLog(OrderManager.class);

    public static final OrderManager me = new OrderManager();

    private OrderManager() {
    }

    public static final OrderManager me() {
        return me;
    }

    private List<OrderFinishedListener> orderFinishedListeners;

    public List<OrderFinishedListener> getOrderFinishedListeners() {
        return orderFinishedListeners;
    }

    public void setOrderFinishedListeners(List<OrderFinishedListener> orderFinishedListeners) {
        this.orderFinishedListeners = orderFinishedListeners;
    }

    public void addOrderFinishedListener(OrderFinishedListener listener) {
        if (orderFinishedListeners == null) {
            synchronized (OrderManager.class) {
                orderFinishedListeners = Collections.synchronizedList(new ArrayList<>());
            }
        }
        orderFinishedListeners.add(listener);
    }

    public void notifyStatusChange(UserOrderItem userOrderItem) {
        if (orderFinishedListeners != null && userOrderItem != null && userOrderItem.isFinished()) {
            for (OrderFinishedListener listener : orderFinishedListeners) {
                try {
                    listener.onFinished(userOrderItem);
                } catch (Exception ex) {
                    LOG.error(ex.toString(), ex);
                }
            }
        }
    }
}
