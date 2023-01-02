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
package io.jpress.core.addon.interceptor;


import com.jfinal.aop.Interceptor;
import io.jboot.utils.ClassUtil;

import java.util.*;

public class AddonInterceptorManager {

    private static Interceptor[] interceptors;
    private static Set<Class<? extends Interceptor>> interceptorClasses = Collections.synchronizedSet(new HashSet<>());

    public static Interceptor[] getInterceptors() {
        return interceptors;
    }

    private static void initInterceptors() {
        synchronized (AddonInterceptorManager.class) {
            Interceptor[] temp = new Interceptor[interceptorClasses.size()];
            int index = 0;
            Iterator<Class<? extends Interceptor>> iterator = interceptorClasses.iterator();
            while (iterator.hasNext()) {
                temp[index++] = ClassUtil.newInstance(iterator.next());
            }
            interceptors = temp;
        }
    }

    public static void addInterceptor(Class<? extends Interceptor> c) {
        interceptorClasses.add(c);
        initInterceptors();
    }

    public static void deleteInterceptor(Class<? extends Interceptor> c) {
        interceptorClasses.removeIf(aClass -> Objects.equals(c.getName(),aClass.getName()));
        initInterceptors();
    }
}
