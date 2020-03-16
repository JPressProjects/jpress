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
package io.jpress.web.commons.express;

import io.jpress.web.commons.express.impl.JuheExpressQuerier;
import io.jpress.web.commons.express.impl.KdniaoExpressQuerier;
import io.jpress.web.commons.express.impl.Kuaidi100ExpressQuerier;
import io.jpress.web.commons.express.impl.ShowapiExpressQuerier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/20
 */
public class ExpressQuerierFactory {

    /**
     * 这个用于后台展示，方便运营人员进行选择
     */
    private static Map<String,String> querierNames = new ConcurrentHashMap<>();
    static {
        querierNames.put("kuaidi100","快递100（kuaidi100.com）");
        querierNames.put("juhecn","聚合数据（juhe.cn）");
        querierNames.put("kdniao","快递鸟（kdniao.com）");
        querierNames.put("showapi","易源接口（showapi.com）");
    }

    private static Map<String,ExpressQuerier> queriers = new ConcurrentHashMap<>();
    static {
        queriers.put("kuaidi100",new Kuaidi100ExpressQuerier());
        queriers.put("juhecn",new JuheExpressQuerier());
        queriers.put("kdniao",new KdniaoExpressQuerier());
        queriers.put("showapi",new ShowapiExpressQuerier());
    }


    public static Map<String, ExpressQuerier> getQueriers() {
        return queriers;
    }

    public static Map<String, String> getQuerierNames() {
        return querierNames;
    }

    public static ExpressQuerier get(String type) {
        return queriers.get(type);
    }
}
