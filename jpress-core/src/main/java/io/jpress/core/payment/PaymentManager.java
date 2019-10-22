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

    public List<PaymentChangeListener> listeners;

    public List<PaymentChangeListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<PaymentChangeListener> listeners) {
        this.listeners = listeners;
    }

    public void addListener(PaymentChangeListener listener) {
        if (listeners == null) {
            synchronized (PaymentManager.class) {
                listeners = Collections.synchronizedList(new ArrayList<>());
            }
        }
        listeners.add(listener);
    }

    public void notifySuccess(PaymentRecord oldPayment, PaymentRecord newPayment) {
        if (listeners != null) {
            for (PaymentChangeListener listener : listeners) {
                try {
                    listener.onChange(oldPayment, newPayment);
                } catch (Exception ex) {
                    LOG.error(ex.toString(), ex);
                }
            }
        }
    }
}
