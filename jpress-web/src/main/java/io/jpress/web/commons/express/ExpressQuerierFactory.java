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
package io.jpress.web.commons.express;

import io.jpress.web.commons.express.impl.JuheExpressQuerier;
import io.jpress.web.commons.express.impl.KdniaoExpressQuerier;
import io.jpress.web.commons.express.impl.Kuaidi100ExpressQuerier;
import io.jpress.web.commons.express.impl.ShowapiExpressQuerier;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/20
 */
public class ExpressQuerierFactory {

    public static ExpressQuerier get(String type) {
        switch (type) {
            case "kuaidi100":
                return new Kuaidi100ExpressQuerier();
            case "juhecn":
                return new JuheExpressQuerier();
            case "kdniao":
                return new KdniaoExpressQuerier();
            case "showapi":
                return new ShowapiExpressQuerier();
            default:
                return null;
        }
    }
}
