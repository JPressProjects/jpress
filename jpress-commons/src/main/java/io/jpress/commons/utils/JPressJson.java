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
package io.jpress.commons.utils;

import com.alibaba.fastjson.JSON;
import com.jfinal.json.JFinalJson;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于Json转化，但是移除所有null值的属性，内容更少
 * @Package io.jpress.commons.utils
 */
public class JPressJson extends JFinalJson {


    @Override
    protected String mapToJson(Map map, int depth) {
        removeNullValueAttrs(map);
        return map == null || map.isEmpty() ? "null" : super.mapToJson(map, depth);
    }


    private void removeNullValueAttrs(Map map) {
        if (map == null) return;
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            if (entry.getValue() == null) {
                iter.remove();
            }
        }
    }


    @Override
    public <T> T parse(String jsonString, Class<T> type) {
        return JSON.parseObject(jsonString, type);
    }
}
