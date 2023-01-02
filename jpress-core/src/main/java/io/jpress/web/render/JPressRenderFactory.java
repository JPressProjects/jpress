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
package io.jpress.web.render;

import com.jfinal.render.Render;
import io.jboot.utils.RequestUtil;
import io.jboot.web.render.JbootRenderFactory;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.web.handler.JPressHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.render
 */
public class JPressRenderFactory extends JbootRenderFactory {


    @Override
    public Render getRedirectRender(String url) {
        return new JPressRedirectRender(url);
    }

    @Override
    public Render getRedirectRender(String url, boolean withQueryString) {
        return new JPressRedirectRender(url, withQueryString);
    }

    @Override
    public Render getErrorRender(int errorCode) {
        if (JPressHandler.getCurrentTarget() != null && JPressHandler.getCurrentTarget().startsWith("/admin/")) {
            return getDefaultErrorRender(errorCode);
        }
        return getTemplateRender(errorCode);
    }


    private Render getTemplateRender(int errorCode) {
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return getDefaultErrorRender(errorCode);
        }

        String errorView = "error_" + errorCode + ".html";
        HttpServletRequest currentRequest = JPressHandler.getCurrentRequest();

        boolean withMobile = currentRequest != null && RequestUtil.isMobileBrowser(currentRequest);
        String view = template.matchView(errorView, withMobile);

        return view == null ? getDefaultErrorRender(errorCode) :
                (currentRequest == null ? new GlobalErrorRender(template.buildRelativePath(view), errorCode)
                        : new TemplateRender(template.buildRelativePath(view), errorCode));
    }


    private Render getDefaultErrorRender(int errorCode){
        if (errorCode == 404) {
            return getErrorRender(errorCode, "/WEB-INF/views/commons/error/404.html");
        } else {
            return getErrorRender(errorCode, "/WEB-INF/views/commons/error/500.html");
        }
    }


}
