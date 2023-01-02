/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.handler.HandlerFactory;
import io.jboot.utils.ClassUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddonHandlerManager {

    private static Handler processHandler = null;
    private static List<Handler> handlers = Collections.synchronizedList(new ArrayList<>());
    private static boolean reset = true;

    public static Handler getProcessHandler(Handler next) {

        if (processHandler == null || reset) {
            synchronized (AddonHandlerManager.class) {
                if (processHandler == null || reset) {
                    processHandler = buildHandler(next);
                    reset = false;
                }
            }
        }
        return processHandler;
    }

    private static synchronized Handler buildHandler(Handler next) {
        // 当没有插件的 handler 的时候，返回原始的 handler
        if (handlers.isEmpty()) {
            return next;
        }

        return HandlerFactory.getHandler(handlers, next);
    }

    public static void addHandler(Class<? extends Handler> c) {
        handlers.removeIf(handler -> ClassUtil.getUsefulClass(handler.getClass()).getName().equals(c.getName()));
        handlers.add(ClassUtil.newInstance(c));
        resetProcessHandler();
    }

    public static void deleteHandler(Class<? extends Handler> c) {
        handlers.removeIf(handler -> ClassUtil.getUsefulClass(handler.getClass()).getName().equals(c.getName()));
        resetProcessHandler();
    }

    private static void resetProcessHandler() {
        reset = true;
    }
}
