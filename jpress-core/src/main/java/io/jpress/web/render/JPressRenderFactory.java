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
package io.jpress.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.TextRender;
import io.jboot.web.JbootControllerContext;
import io.jboot.web.render.JbootRenderFactory;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.render
 */
public class JPressRenderFactory extends JbootRenderFactory {

    @Override
    public Render getErrorRender(int errorCode) {

        /**
         * 如果是后台、api等其他非模板相关Controller，不用此Factory渲染。
         */
        if (!(JbootControllerContext.get() instanceof TemplateControllerBase)) {
            return super.getErrorRender(errorCode);
        }

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return new TextRender(errorCode + " error, bug can not find current template to render");
        }

        String view = template.matchTemplateFile("error_" + errorCode + ".html",((TemplateControllerBase) JbootControllerContext.get()).isMoblieBrowser());
        if (view == null) {
            return super.getErrorRender(errorCode);
        }

        view = "/templates/" + template.getFolder() + "/" + view;
        return new TemplateRender(view);
    }


}
