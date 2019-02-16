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
package io.jpress.module.article.ext;


import io.jpress.module.article.ext.renders.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Field 渲染器，目的是生成 html 的内容
 */
public class ArticleFieldRenderFactory {

    private static Map<String, ArticleFieldRender> renderMap = new ConcurrentHashMap<>();

    static {
        registerRender(ArticleField.TYPE_INPUT, new InputRenader());
        registerRender(ArticleField.TYPE_TEXTAREA, new TextareaRenader());
        registerRender(ArticleField.TYPE_SELECT, new SelectRenader());
        registerRender(ArticleField.TYPE_CHECKBOX, new CheckboxRenader());
        registerRender(ArticleField.TYPE_SWITCH, new SwitchRenader());
    }

    public static void registerRender(String type, ArticleFieldRender render) {
        renderMap.put(type, render);
    }

    public static ArticleFieldRender getRender(String type) {
        return renderMap.get(type);
    }
}
