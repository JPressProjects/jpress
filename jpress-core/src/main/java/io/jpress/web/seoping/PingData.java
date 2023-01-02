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
package io.jpress.web.seoping;


import java.util.ArrayList;
import java.util.List;

public class PingData {

    private List<String> datas;

    public static PingData create() {
        return new PingData();
    }


    public static PingData create(String... data) {
        PingData d = create();
        for (String s : data) {
            d.add(s);
        }
        return d;
    }


    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
    }

    public PingData add(String data) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.add(data);
        return this;
    }
}
