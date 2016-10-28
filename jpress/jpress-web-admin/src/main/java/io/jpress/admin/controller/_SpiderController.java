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
import io.jpress.model.query.ContentQuery;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.spider.SpiderHandler;

import java.math.BigInteger;

@RouterMapping(url = "/admin/spider")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SpiderController extends JBaseController {

    public void index() {
//        BigInteger id = getParaToBigInteger("id");
//        if (null != id) {
//            setAttr("content", ContentQuery.me().findById(id));
//        }
//        List<Content> contents = ContentQuery.me().findByModule(Consts.MODULE_API_APPLICATION);
            setAttr("isRunning", SpiderHandler.getSpiderHandler().isRunning()?"开启":"关闭");
        render("/WEB-INF/admin/option/spider.html");
    }

    @Before(UCodeInterceptor.class)
    public void open() {
//        BigInteger id = getParaToBigInteger("id");
//        if (id != null) {
//            ContentQuery.me().deleteById(id);
        renderAjaxResultForSuccess("开启成功");
        SpiderHandler.getSpiderHandler().startSpiders();
//        } else {
//            renderAjaxResultForError();
//        }
    }

    @Before(UCodeInterceptor.class)
    public void close() {
        BigInteger id = getParaToBigInteger("id");
        if (id != null) {
            ContentQuery.me().deleteById(id);
            renderAjaxResultForSuccess("关闭成功");
        } else {
            renderAjaxResultForError();
        }
    }
}
