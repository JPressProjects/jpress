/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.admin.controller;

import com.jfinal.aop.Before;
import io.jpress.core.JBaseController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.interceptor.UCodeInterceptor;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.spider.SpiderHandler;

@RouterMapping(url = "/admin/spider")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SpiderController extends JBaseController {

    public void index() {
        setAttr("isRunning", SpiderHandler.getSpiderHandler().isRunning() ? "开启" : "关闭");
        render("/WEB-INF/admin/option/spider.html");
    }

    @Before(UCodeInterceptor.class)
    public void open() {
        SpiderHandler.getSpiderHandler().startSpiders();
        setAttr("isRunning", SpiderHandler.getSpiderHandler().isRunning() ? "开启" : "关闭");
        renderAjaxResultForSuccess();
//        render("/WEB-INF/admin/option/spider.html");
    }

    @Before(UCodeInterceptor.class)
    public void close() {
        SpiderHandler.getSpiderHandler().stopSpiders();
        setAttr("isRunning", SpiderHandler.getSpiderHandler().isRunning() ? "开启" : "关闭");
        renderAjaxResultForSuccess();
    }
}
