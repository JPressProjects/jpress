/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.admin.controller;

import com.google.inject.Inject;
import com.jfinal.aop.Before;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.service.ContentService;
import io.jpress.web.admin.interceptor._AdminInterceptor;

/**
 * 文章等内容相关插件
 */
@RequestMapping(value = "/admin/content", viewPath = "/WEB-INF/admin/content")
@Before(_AdminInterceptor.class)
public class _ContentController extends JbootController {

    @Inject
    ContentService contentService;

    /**
     * 后台首页
     */
    public void index() {
        render("index.html");
    }


}
