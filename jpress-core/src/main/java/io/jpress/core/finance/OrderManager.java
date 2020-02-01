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
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import io.jpress.core.finance.render.AdminOrderDetailPageRender;
import io.jpress.core.finance.render.DefaultAdminOrderDetailPageRender;
import io.jpress.core.finance.render.DefaultUCenterOrderDetailPageRender;
import io.jpress.core.finance.render.UCenterOrderDetailPageRender;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang
 */
public class OrderManager {

    private static final Log LOG = Log.getLog(OrderManager.class);

    public static final OrderManager me = new OrderManager();

    private OrderManager() {
    }

    public static final OrderManager me() {
        return me;
    }

    private List<OrderItemStatusChangeListener> orderItemStatusChangeListeners;
    private List<OrderStatusChangeListener> orderStatusChangeListeners;

    private Map<String, AdminOrderDetailPageRender> adminOrderDetailPageRenderMap = new ConcurrentHashMap<>();
    private Map<String, UCenterOrderDetailPageRender> uCenterOrderDetailPageRenderMap = new ConcurrentHashMap<>();

    public List<OrderItemStatusChangeListener> getOrderItemStatusChangeListeners() {
        return orderItemStatusChangeListeners;
    }

    public void setOrderItemStatusChangeListeners(List<OrderItemStatusChangeListener> orderItemStatusChangeListeners) {
        this.orderItemStatusChangeListeners = orderItemStatusChangeListeners;
    }

    public void addOrderItemStatusChangeListener(OrderItemStatusChangeListener listener) {
        if (orderItemStatusChangeListeners == null) {
            synchronized (OrderManager.class) {
                orderItemStatusChangeListeners = Collections.synchronizedList(new ArrayList<>());
            }
        }
        orderItemStatusChangeListeners.add(Aop.inject(listener));
    }

    public List<OrderStatusChangeListener> getOrderStatusChangeListeners() {
        return orderStatusChangeListeners;
    }

    public void setOrderStatusChangeListeners(List<OrderStatusChangeListener> orderItemStatusChangeListeners) {
        this.orderStatusChangeListeners = orderItemStatusChangeListeners;
    }

    public void addOrderStatusChangeListener(OrderStatusChangeListener listener) {
        if (orderStatusChangeListeners == null) {
            synchronized (OrderManager.class) {
                orderStatusChangeListeners = Collections.synchronizedList(new ArrayList<>());
            }
        }
        orderStatusChangeListeners.add(Aop.inject(listener));
    }

    public void notifyItemStatusChanged(UserOrderItem userOrderItem) {
        if (orderItemStatusChangeListeners != null && userOrderItem != null) {
            for (OrderItemStatusChangeListener listener : orderItemStatusChangeListeners) {
                try {
                    listener.onStatusChanged(userOrderItem);
                } catch (Exception ex) {
                    LOG.error(ex.toString(), ex);
                }
            }
        }
    }

    public void notifyOrderStatusChanged(UserOrder order) {
        if (orderStatusChangeListeners != null && order != null) {
            for (OrderStatusChangeListener listener : orderStatusChangeListeners) {
                try {
                    listener.onStatusChanged(order);
                } catch (Exception ex) {
                    LOG.error(ex.toString(), ex);
                }
            }
        }
    }

    public void registerAdminOrderDetailRender(String forProductType, AdminOrderDetailPageRender render) {
        adminOrderDetailPageRenderMap.put(forProductType, render);
    }

    public void registerUCenterOrderDetailRender(String forProductType, UCenterOrderDetailPageRender render) {
        uCenterOrderDetailPageRenderMap.put(forProductType, render);
    }

    public Map<String, AdminOrderDetailPageRender> getAdminOrderDetailPageRenderMap() {
        return adminOrderDetailPageRenderMap;
    }

    public Map<String, UCenterOrderDetailPageRender> getuCenterOrderDetailPageRenderMap() {
        return uCenterOrderDetailPageRenderMap;
    }

    public void renderAdminOrderDetailPage(Controller controller, UserOrder order) {
        AdminOrderDetailPageRender render = adminOrderDetailPageRenderMap.get(order.getProductType());
        if (render == null) {
            render = DefaultAdminOrderDetailPageRender.me;
        }
        render.doRender(controller, order);
    }


    public void renderUCenterOrderDetailPage(Controller controller, UserOrder order) {
        UCenterOrderDetailPageRender render = uCenterOrderDetailPageRenderMap.get(order.getProductType());
        if (render == null) {
            render = DefaultUCenterOrderDetailPageRender.me;
        }
        render.doRender(controller, order);
    }
}
