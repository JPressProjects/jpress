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

import com.jfinal.core.Action;
import io.jboot.web.handler.JbootActionHandler;


public class AddonControllerProcesser extends JbootActionHandler {

    private static final String INDEX_ACTION_KEY = "/";

    @Override
    public Action getAction(String target, String[] urlPara) {
        Action action = super.getAction(target, urlPara);
        if (action == null) {
            return AddonControllerManager.getAction(target, urlPara);
        }

        if (!INDEX_ACTION_KEY.equals(target) && INDEX_ACTION_KEY.equals(action.getActionKey())) {
            Action addonAction = AddonControllerManager.getAction(target, urlPara);
            if (addonAction != null) return addonAction;
        }

        return action;
    }
}
