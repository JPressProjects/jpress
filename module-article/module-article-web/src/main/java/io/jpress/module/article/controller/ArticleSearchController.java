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
package io.jpress.module.article.controller;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/article/search")
public class ArticleSearchController extends TemplateControllerBase {


    public void index() {

        /**
         * 不让页面大于100，我认为：
         * 1、当一个真实用户在搜索某个关键字的内容，通过翻页去找对应数据，不可能翻到100页以上。
         * 2、翻页翻到100页以上，一般是机器：可能是来抓取数据的。
         */
        int page = getParaToInt("page", 1);
        if (page <= 0 || page > 100) {
            renderError(404);
            return;
        }

        setAttr("keyword", getEscapeHtmlPara("keyword"));
        setAttr("page", page);

        setMenuActive(menu -> menu.isUrlStartWidth("/article/search"));
        render("artsearch.html");
    }


}
