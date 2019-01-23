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
package io.jpress.core.addon.controller;


import com.jfinal.config.Routes;
import com.jfinal.core.Action;
import com.jfinal.core.ActionMapping;
import com.jfinal.core.Controller;
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AddonControllerManager {

    private static Routes routes = new Routes() {
        @Override
        public void config() {
            this.setClearAfterMapping(false);
        }

    };
    private static AddonActionMapping actionMapping = new AddonActionMapping(routes);

    public static void addController(Class<? extends Controller> controllerClass) {
        RequestMapping mapping = controllerClass.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        String viewPath = AnnotationUtil.get(mapping.viewPath());
        if (StrUtil.isBlank(viewPath)) {
            routes.add(value, controllerClass);
        } else {
            routes.add(value, controllerClass, viewPath);
        }
    }

    public static void buildActionMapping() {
        actionMapping.buildActionMapping();
    }

    public static Action getAction(String target, String[] urlPara) {
        return actionMapping.getAction(target, urlPara);
    }

    public static void deleteController(Class<? extends Controller> c) {
        RequestMapping mapping = c.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        actionMapping.deleteAction(value);
    }

    public static class AddonActionMapping extends ActionMapping {

        public AddonActionMapping(Routes routes) {
            super(routes);
            routes.config();
            this.mapping = new ConcurrentHashMap<>();
        }

        @Override
        public void buildActionMapping() {
            super.buildActionMapping();
        }

        public void deleteAction(String target) {
            this.mapping.remove(target);
            this.routes.getRouteItemList().removeIf(route -> route.getControllerKey().equals(target));
            try {
                Field field = Routes.class.getDeclaredField("controllerKeySet");
                field.setAccessible(true);
                Set<String> routes = (Set<String>) field.get(null);
                routes.remove(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Action getAction(String url, String[] urlPara) {
            return super.getAction(url, urlPara);
        }
    }
}
