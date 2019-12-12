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


import com.jfinal.aop.Aop;
import com.jfinal.log.Log;
import io.jpress.model.PaymentRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaymentManager {

    private static final Log LOG = Log.getLog(PaymentManager.class);

    public static final PaymentManager me = new PaymentManager();

    private PaymentManager() {
    }

    public static final PaymentManager me() {
        return me;
    }

    private List<PaymentSuccessListener> listeners;

    public List<PaymentSuccessListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<PaymentSuccessListener> listeners) {
        this.listeners = listeners;
    }

    public void addListener(PaymentSuccessListener listener) {
        if (listeners == null) {
            synchronized (PaymentManager.class) {
                listeners = Collections.synchronizedList(new ArrayList<>());
            }
        }
        listeners.add(Aop.inject(listener));
    }

    public void notifySuccess(PaymentRecord payment) {
        if (listeners != null && payment != null && payment.isPaySuccess()) {
            for (PaymentSuccessListener listener : listeners) {
                try {
                    listener.onSuccess(payment);
                } catch (Exception ex) {
                    LOG.error(ex.toString(), ex);
                }
            }
        }
    }
}
