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
package io.jpress.web.commons.textfilter.impl;

import io.jpress.web.commons.textfilter.TextFilter;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 * 小花儿AI 文本垃圾过滤器
 * https://market.aliyun.com/products/57124001/cmapi029968.html
 */
public class XiaohuaerAITextFilter implements TextFilter {

    @Override
    public boolean check(String text) {
        return false;
    }
}
