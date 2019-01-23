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
package io.jpress.core.addon.handler;


import com.jfinal.handler.Handler;
import io.jboot.utils.ClassUtil;

import java.util.*;

public class AddonHandlerManager {

    private static List<Handler> handlers;
    private static Set<Class<? extends Handler>> handlerClasses = Collections.synchronizedSet(new HashSet<>());


    public static List<Handler> getHandlers() {
        if (handlerClasses.isEmpty()) {
            return null;
        }

        if (!handlerClasses.isEmpty()) {
            if (handlers == null || handlers.size() != handlerClasses.size()) {
                initHandlers();
            }
        }

        return handlers;
    }

    private static void initHandlers() {
        synchronized (AddonHandlerManager.class) {
            if (handlers != null && handlers.size() == handlerClasses.size()) {
                return;
            }

            List<Handler> temp = new ArrayList<>();
            Iterator<Class<? extends Handler>> iterator = handlerClasses.iterator();
            while (iterator.hasNext()) temp.add(ClassUtil.newInstance(iterator.next()));

            handlers = temp;
        }
    }

    public static void addHandler(Class<? extends Handler> c) {
        handlerClasses.add(c);
    }

    public static void deleteHandler(Class<? extends Handler> c) {
        handlerClasses.remove(c);
    }
}
