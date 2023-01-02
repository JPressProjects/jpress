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
package io.jpress.web.front;

import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.handler.JPressHandler;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/")
public class IndexController extends TemplateControllerBase {


    public void index() {

        //如果不是首页 / ，使用 page 模块去处理
        if (!"/".equals(JPressHandler.getCurrentTarget())) {
            forwardAction("/page");
            return;
        }

        //设置菜单高亮
        setMenuActive(menu -> "/".equals(menu.getUrl()));

        String indexView = StrUtil.isBlank(JPressOptions.getIndexStyle())
                ? "index.html"
                : "index_" + JPressOptions.getIndexStyle() + ".html";


        //渲染 模板下的 index.html
        render(indexView);
    }

}
