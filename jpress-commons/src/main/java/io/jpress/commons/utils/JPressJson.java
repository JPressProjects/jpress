/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.google.common.collect.Lists;
import io.jboot.utils.StrUtil;
import io.jboot.web.json.JbootJson;
import io.jpress.JPressOptions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用于Json转化，但是移除所有null值的属性，内容更少
 * @Package io.jpress.commons.utils
 */
public class JPressJson extends JbootJson {

    private static final List<String> needAddDomainAttrs = Lists.newArrayList("avatar", "thumbnail");

    //不能定义为静态的，每次应该从 JPress 获取最新的后台配置
    String resDomain = JPressOptions.getResDomain();

    public JPressJson() {
        super();
    }

    /**
     * 优化 map 的属性
     *
     * @param map
     */
    @Override
    protected void optimizeMapAttrs(Map map) {
        if (map == null || map.isEmpty()) {
            return;
        }

        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {

            Map.Entry entry = (Map.Entry) iter.next();

            // 给图片类的属性，添加域名成为绝对路径，
            // 相对路径放到app上去麻烦
            // 这个行为会改变model的值（涉及到改变缓存的值）
            if (StrUtil.isNotBlank(resDomain) && needAddDomainAttrs.contains(entry.getKey())) {
                String value = (String) entry.getValue();
                if (StrUtil.isNotBlank(value) && !value.startsWith("http")) {
                    entry.setValue(resDomain + value);
                }
            }

        }
    }
}
