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


import com.jfinal.render.RenderManager;
import io.jboot.utils.StrUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


public class AttachmentHandlerKit {


    public static void handle(String baseRoot, String target
            , HttpServletRequest request
            , HttpServletResponse response
            , boolean[] isHandled) {


        //不处理默认情况，由 web 容器去处理
        if (StrUtil.isBlank(baseRoot)) {
            return;
        }

        try {
            isHandled[0] = true;

            if (target.endsWith("/") || !target.contains(".") || target.toLowerCase().contains(".jsp")) {
                response.sendError(404);
                return;
            }

            File file = new File(baseRoot, target);
            if (!file.exists()) {
                response.sendError(404);
                return;
            }

            RenderManager.me().getRenderFactory()
                    .getFileRender(file)
                    .setContext(request, response)
                    .render();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

