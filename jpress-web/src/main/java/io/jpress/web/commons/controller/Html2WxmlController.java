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
package io.jpress.web.commons.controller;

import com.jfinal.kit.StrKit;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.web.base.ApiControllerBase;
import io.jpress.web.commons.controller.html2wxml.HtmlToJson;
import io.jpress.web.commons.controller.html2wxml.Params;

import java.util.Collections;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/commons/html2wxml")
public class Html2WxmlController extends ApiControllerBase {

    public void index() {
        String html = getPara("html"); // 要求html内容必须通过post传过来
        Params params = getParams();
        String resultJson = HtmlToJson.by(html, params).get();

        if (StrKit.isBlank(resultJson)) {
            renderJson(Collections.emptyList());
        } else {
            renderJson(resultJson);
        }
    }


    /**
     * 获取转换条件
     *
     * @return
     */
    private Params getParams() {
        //类型 默认HMTL
        String type = getPara("type", Params.TYPE_HTML);
        //是否开启pre代码高亮 默认开启
        Boolean highlight = getParaToBoolean("highlight", true);
        //是否开启pre代码行号 默认开启
        Boolean linenums = getParaToBoolean("linenums", true);
        //获取a和img静态资源的根路径URL
        String baseUri = StrUtil.obtainDefaultIfBlank(JPressOptions.getCDNDomain(),getBaseUrl());

        Params params = new Params();
        params.setHighlight(highlight);
        params.setLinenums(linenums);
        params.setType(type);
        params.setBaseUri(baseUri);

        return params;
    }


}
