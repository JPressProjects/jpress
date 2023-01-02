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
package io.jpress.core.support.smartfield;


import io.jpress.core.support.smartfield.renders.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Field 渲染器，目的是生成 html 的内容
 */
public class SmartFieldRenderFactory {

    private static Map<String, SmartFieldRender> renderMap = new ConcurrentHashMap<>();

    static {
        registerRender(SmartField.TYPE_INPUT, new InputRender());
        registerRender(SmartField.TYPE_TEXTAREA, new TextareaRender());
        registerRender(SmartField.TYPE_SELECT, new SelectRender());
        registerRender(SmartField.TYPE_CHECKBOX, new CheckboxRender());
        registerRender(SmartField.TYPE_SWITCH, new SwitchRender());
        registerRender(SmartField.TYPE_RADIO, new RadioRender());
        registerRender(SmartField.TYPE_IMAGE, new ImageRender());
        registerRender(SmartField.TYPE_FILE, new FileRender());
        registerRender(SmartField.TYPE_DATE, new DateRender());
        registerRender(SmartField.TYPE_DATETIME, new DatetimeRender());
    }


    public static void registerRender(String type, SmartFieldRender render) {
        renderMap.put(type, render);
    }

    public static SmartFieldRender getRender(String type) {
        return renderMap.get(type);
    }

    public static Map<String, SmartFieldRender> getRenderMap() {
        return renderMap;
    }
}
