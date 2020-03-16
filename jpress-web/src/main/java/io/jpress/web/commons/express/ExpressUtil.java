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


import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

import java.util.List;

public class ExpressUtil {

    /**
     * 快递查询
     *
     * @param expressCompanyCode 快递公司
     * @param num                快递单号
     * @return
     */
    public static List<ExpressInfo> queryExpress(String expressCompanyCode, String num) {
        if (!StrUtil.areNotEmpty(expressCompanyCode, num)) {
            return null;
        }

        String type = JPressOptions.get("express_api_type");
        //没有设置快递接口的时候
        if (type == null) {
            return null;
        }
        ExpressQuerier querier = ExpressQuerierFactory.get(type);

        if (querier == null) {
            return null;
        }

        ExpressCompany company = ExpressCompany.getByCode(expressCompanyCode);
        return querier.query(company, num);
    }

}

