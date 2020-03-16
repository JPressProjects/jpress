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
package io.jpress.web.handler;


import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public static HttpServletRequest getCurrentRequest() {
        return requestContext.get();
    }

    private static final String ADDON_TARGET_PREFIX = "/addons";
    private static final String TEMPLATES_TARGET_PREFIX = "/templates";
    private static final String ATTACHMENT_TARGET_PREFIX = "/attachment";


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        //不让访问 插件目录 下的 .html、 .sql 文件 和 WEB-INF 目录下的任何文件
        if (target.startsWith(ADDON_TARGET_PREFIX)) {
            if (target.endsWith(".html")
                    || target.endsWith(".sql")
                    || target.contains("WEB-INF")) {
                HandlerKit.renderError404(request, response, isHandled);
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

        //附件目录
        if (target.startsWith(ATTACHMENT_TARGET_PREFIX)) {
            AttachmentHandlerKit.handle(target,request,response,isHandled);
            return;
        }


        String suffix = JPressOptions.getAppUrlSuffix();
        if (StrUtil.isBlank(suffix)  // 不启用伪静态
                && target.indexOf('.') != -1) {
            //return 表示让服务器自己去处理
            return;
        }

        //启用伪静态
        if (StrUtil.isNotBlank(suffix) && target.endsWith(suffix)) {
            target = target.substring(0, target.length() - suffix.length());
        }

        try {
            targetContext.set(target);
            requestContext.set(request);
            request.setAttribute("VERSION", JPressConsts.VERSION);
            request.setAttribute("CPATH", request.getContextPath());
            next.handle(target, request, response, isHandled);
        } finally {
            targetContext.remove();
            requestContext.remove();
        }
    }


}
