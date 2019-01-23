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
package io.jpress.core.addon.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.web.fixedinterceptor.FixedInterceptor;
import io.jboot.web.fixedinterceptor.FixedInvocation;

import java.lang.reflect.Method;

public class AddonInterceptorProcesser implements FixedInterceptor {

    @Override
    public void intercept(FixedInvocation fixedInvocation) {

        Interceptor[] interceptors = AddonInterceptorManager.getInterceptors();

        if (interceptors == null || interceptors.length == 0) {
            fixedInvocation.invoke();
        } else {
            new AddonInvocation(fixedInvocation, interceptors).invoke();
        }
    }


    public class AddonInvocation extends Invocation {

        private FixedInvocation invocation;
        private Interceptor[] inters;

        private int index = 0;


        public AddonInvocation(FixedInvocation invocation, Interceptor[] interceptors) {
            this.invocation = invocation;
            this.inters = interceptors;
        }


        public void invoke() {
            if (index < inters.length) {
                inters[index++].intercept(this);
            } else if (index++ == inters.length) {    // index++ ensure invoke action only one time
                invocation.invoke();
            }
        }


        public Method getMethod() {
            return invocation.getMethod();
        }

        public Controller getController() {
            return invocation.getController();
        }

        public String getActionKey() {
            return invocation.getActionKey();
        }

        public String getControllerKey() {
            return invocation.getControllerKey();
        }

        public String getMethodName() {
            return invocation.getMethodName();
        }

        @Override
        public String getViewPath() {
            return invocation.getInvocation().getViewPath();
        }
    }


}
