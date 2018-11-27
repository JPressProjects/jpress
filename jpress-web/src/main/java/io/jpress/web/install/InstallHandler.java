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
package io.jpress.web.install;

import com.jfinal.handler.Handler;
import io.jpress.core.install.JPressInstaller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class InstallHandler extends Handler {

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (JPressInstaller.isInstalled()){
            next.handle(target,request,response,isHandled);
            return;
        }

        if (target.indexOf('.') != -1) {
            return ;
        }

        if (!target.startsWith("/install")){
            next.handle("/install",request,response,isHandled);
        }else {
            next.handle(target,request,response,isHandled);
        }



//        isHandled[0] = true;
//
//        doRender(request,response);

    }

//    private void doRender(HttpServletRequest request, HttpServletResponse response) {
//
//        String[] urlPara = {null};
//        Action action = JFinal.me().getAction("/install", urlPara);
//
//        InstallController controller = new InstallController();
//        try {
//            CPI._init_(controller, action, request, response, urlPara[0]);
//
//            Render render = controller.getRender();
//
//            if (render == null) {
//                render = RenderManager.me().getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
//            }
//
//            render.setContext(request, response, action.getViewPath()).render();
//        }finally {
//            CPI._clear_(controller);
//        }
//
//
//    }

}
