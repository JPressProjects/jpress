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
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.aop.InterceptorBuilder;
import io.jboot.aop.Interceptors;
import io.jboot.aop.annotation.AutoLoad;
import io.jboot.utils.ClassUtil;

import java.lang.reflect.Method;

@AutoLoad
public class AddonInterceptorProcesser implements Interceptor, InterceptorBuilder {

    @Override
    public void intercept(Invocation invocation) {

        Interceptor[] interceptors = AddonInterceptorManager.getInterceptors();

        if (interceptors == null || interceptors.length == 0) {
            invocation.invoke();
        } else {
            new AddonInvocation(invocation, interceptors).invoke();
        }
    }

    @Override
    public void build(Class<?> serviceClass, Method method, Interceptors interceptors) {
        if (Controller.class.isAssignableFrom(serviceClass)){
            interceptors.add(this);
        }
    }


    public static class AddonInvocation extends Invocation {

        private Invocation invocation;
        private Interceptor[] inters;

        private int index = 0;


        public AddonInvocation(Invocation invocation, Interceptor[] interceptors) {
            this.invocation = invocation;
            this.inters = interceptors;
        }


        @Override
        public void invoke() {
            if (index < inters.length) {
                Interceptor interceptor = inters[index++];
                try {
                    interceptor.intercept(this);
                }finally {
                    if (Jboot.isDevMode()){
                        System.out.println("addon interceptor invoked: " + ClassUtil.getUsefulClass(interceptor.getClass()));
                    }
                }

            } else if (index++ == inters.length) {
                invocation.invoke();
            }
        }


        @Override
        public Object getArg(int index) {
            return invocation.getArg(index);
        }

        @Override
        public void setArg(int index, Object value) {
            invocation.setArg(index, value);
        }

        @Override
        public Object[] getArgs() {
            return invocation.getArgs();
        }

        @Override
        public <T> T getTarget() {
            return invocation.getTarget();
        }

        @Override
        public Method getMethod() {
            return invocation.getMethod();
        }

        @Override
        public String getMethodName() {
            return invocation.getMethodName();
        }

        @Override
        public <T> T getReturnValue() {
            return invocation.getReturnValue();
        }

        @Override
        public void setReturnValue(Object returnValue) {
            invocation.setReturnValue(returnValue);
        }

        @Override
        public Controller getController() {
            return invocation.getController();
        }

        @Override
        public String getActionKey() {
            return invocation.getActionKey();
        }

        @Override
        public String getControllerKey() {
            return invocation.getControllerKey();
        }

        @Override
        public String getViewPath() {
            return invocation.getViewPath();
        }

        @Override
        public boolean isActionInvocation() {
            return invocation.isActionInvocation();
        }
    }


}
