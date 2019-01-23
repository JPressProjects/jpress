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
package io.jpress.core.addon.handler;


import com.jfinal.handler.Handler;
import com.jfinal.handler.HandlerFactory;
import com.jfinal.kit.HandlerKit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class AddonHandlerProcesser extends Handler {


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        //不让访问 插件目录 下的 .html 文件
        if (target.startsWith("/addons") && target.endsWith(".html")) {
            HandlerKit.renderError404(request, response, isHandled);
            return;
        }

        List<Handler> list = AddonHandlerManager.getHandlers();

        if (list == null || list.isEmpty()) {
            next.handle(target, request, response, isHandled);
            return;
        }

        Handler handler = HandlerFactory.getHandler(list, new Handler() {
            @Override
            public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
                AddonHandlerProcesser.this.next.handle(target, request, response, isHandled);
            }
        });

        handler.handle(target, request, response, isHandled);
    }


}
