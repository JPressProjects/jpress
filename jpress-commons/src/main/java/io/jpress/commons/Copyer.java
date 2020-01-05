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
package io.jpress.commons;


import io.jboot.db.model.JbootModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 由于 ehcache 缓存有这么一个特征：从缓存读取出来的缓存数据不能被再次修改，否则会出错
 * 因此，在某些场景下，我们读取的数据需要再次被修改，需要从缓存读取数据后 copy一份新的返回
 */
public class Copyer {

    public static <M extends JbootModel> List<M> copy(List<M> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        List<M> rlist = new ArrayList<>(list.size());
        list.forEach(m -> rlist.add((M) m.copy()));
        return rlist;
    }
}
