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
package io.jpress.core.addon.controller;

import com.jfinal.core.Action;
import io.jboot.web.handler.JbootActionHandler;
import io.jpress.JPressConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AddonControllerProcesser extends JbootActionHandler {

    private static final ThreadLocal<Boolean> addonControllerFlags= new ThreadLocal<>();

    public static final String getBaseTemplatePath(String originalBasePath){
        Boolean flag = addonControllerFlags.get();
        return flag != null && flag ? JPressConfig.me.getAddonRoot(): originalBasePath;
    }

    @Override
    public Action getAction(String target, String[] urlPara, HttpServletRequest request) {
        return getAction(target, urlPara);
    }

    @Override
    public Action getAction(String target, String[] urlPara) {
        Action action = super.getAction(target, urlPara);
        if (action == null) {
            addonControllerFlags.set(true);
            return AddonControllerManager.getAction(target, urlPara);
        }

        if (!target.equals(action.getActionKey())) {
            Action addonAction = AddonControllerManager.getAction(target, urlPara);
            if (addonAction != null && target.equals(addonAction.getActionKey())) {
                addonControllerFlags.set(true);
                return addonAction;
            }
        }
        return action;
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        try {
            super.handle(target, request, response, isHandled);
        }finally {
            addonControllerFlags.remove();
        }
    }
}
