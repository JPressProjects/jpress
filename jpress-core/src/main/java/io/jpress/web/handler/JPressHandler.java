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
package io.jpress.web.handler;


import com.google.common.collect.Sets;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConfig;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 伪静态处理器
 * @Package io.jpress.web.handler
 */

public class JPressHandler extends Handler {

    private static final ThreadLocal<String> targetContext = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletRequest> requestContext = new ThreadLocal<>();

    public static String getCurrentTarget() {
        return targetContext.get();
    }

    public static void setCurrentTarget(String target) {
        targetContext.set(target);
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestContext.get();
    }

    private static final String ADDON_TARGET_PREFIX = "/addons";
    private static final String TEMPLATES_TARGET_PREFIX = "/templates";
    private static final String ATTACHMENT_TARGET_PREFIX = "/attachment";

    private static final Set<String> v3CssPaths = Sets.newHashSet("/static/commons/article.css"
            , "/static/commons/product.css"
            , "/static/commons/page.css");

    private static final Set<String> v3JsPaths = Sets.newHashSet("/static/commons/article.js"
            , "/static/commons/product.js"
            , "/static/commons/page.js");


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        try {
            setCurrentTarget(target);
            requestContext.set(request);
            request.setAttribute("VERSION", JPressConsts.VERSION);
            request.setAttribute("CPATH", request.getContextPath());
            request.setAttribute("CURRENT_TIME", System.currentTimeMillis());

            // SPATH 默认值为 "" 空字符串
            request.setAttribute("SPATH", "");
            doHandle(target, request, response, isHandled);
        } finally {
            targetContext.remove();
            requestContext.remove();
        }
    }


    public void doHandle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        //不让访问 插件目录 下的 .html、 .sql 文件 和 WEB-INF 目录下的任何文件
        if (target.startsWith(ADDON_TARGET_PREFIX)) {
            if (target.endsWith(".html")
                    || target.endsWith(".sql")
                    || target.contains("WEB-INF")) {
                HandlerKit.renderError404(request, response, isHandled);
                return;
            } else if (target.contains(".")) {
                    AttachmentHandlerKit.handle(JPressConfig.me.getAddonRoot(), target, request, response, isHandled);
                return;
            }
        }

        //不让访问 模板目录 下的 .html 文件
        if (target.startsWith(TEMPLATES_TARGET_PREFIX)) {
            if (target.endsWith(".html")) {
                HandlerKit.renderError404(request, response, isHandled);
                return;
            }
        }

        //v4 版本已经合并了 v3 版本的 css 到 jpressfront.css 了
        //需要重定向，否则无法兼容 v3 的模板
        if (target.endsWith(".css") && v3CssPaths.contains(target)) {
            HandlerKit.redirect301(request.getContextPath() + "/static/front/jpressfront.css", request, response, isHandled);
            return;
        }

        //v4 版本已经合并了 v3 版本的 js 到 jpressfront.js 了
        //需要重定向，否则无法兼容 v3 的模板
        if (target.endsWith(".js") && v3JsPaths.contains(target)) {
            HandlerKit.redirect301(request.getContextPath() + "/static/front/jpressfront.js", request, response, isHandled);
            return;
        }

        //附件目录
        if (target.startsWith(ATTACHMENT_TARGET_PREFIX)) {
            AttachmentHandlerKit.handle(JPressConfig.me.getAttachmentRoot(), target, request, response, isHandled);
            return;
        }

        //如果是访问 .html ，直接去除后缀
        if (target.contains(".") && target.endsWith(".html")) {
            target = target.substring(0, target.length() - 5);
            setCurrentTarget(target);
        }


        String suffix = JPressOptions.getAppUrlSuffix();

        //若不启用伪静态，让 undertow 处理静态资源 css js 等
        if (StrUtil.isBlank(suffix) && target.indexOf('.') != -1) {
            return;
        }

        //启用伪静态
        if (StrUtil.isNotBlank(suffix) && target.endsWith(suffix)) {
            target = target.substring(0, target.length() - suffix.length());
            setCurrentTarget(target);
        }

        next.handle(target, request, response, isHandled);
    }


}
