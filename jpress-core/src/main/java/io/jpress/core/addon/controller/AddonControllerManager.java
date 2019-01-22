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
import io.jboot.utils.AnnotationUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.addon.AddonInfo;

public class AddonControllerManager {

    private static Routes routes = new Routes() {
        @Override
        public void config() {

        }
    };
    private static AddonActionMapping actionMapping = new AddonActionMapping(routes);

    private static void addController(Class<? extends AddonController> controllerClass, AddonInfo addonInfo) {
        RequestMapping mapping = controllerClass.getAnnotation(RequestMapping.class);
        if (mapping == null) return;

        String value = AnnotationUtil.get(mapping.value());
        if (value == null) return;

        String viewPath = AnnotationUtil.get(mapping.viewPath());
        if (StrUtil.isBlank(viewPath)) {
            viewPath = "addons/" + addonInfo.getId();
        }

        routes.add(value, controllerClass, viewPath);
    }

    public static void buildActionMapping() {
        actionMapping.buildActionMapping();
    }

    public static Action getAction(String target, String[] urlPara) {
        return actionMapping.getAction(target, urlPara);
    }

    public static void addController(Class<? extends AddonController> c) {
    }

    public static void deleteController(Class<? extends AddonController> c) {
    }

    public static class AddonActionMapping extends ActionMapping {

        public AddonActionMapping(Routes routes) {
            super(routes);
        }

        @Override
        public void buildActionMapping() {
            super.buildActionMapping();
        }

    }
}
