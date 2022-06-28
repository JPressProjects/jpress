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
package io.jpress.web.directive;

import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.ext.MixedByteArrayOutputStream;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.TemplateBlockInfo;
import io.jpress.service.TemplateBlockInfoService;
import io.jpress.web.render.TemplateRender;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("blockContainer")
public class BlockContainerDirective extends JbootDirectiveBase {

    @Inject
    private TemplateBlockInfoService blockInfoService;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String templateId = TemplateManager.me().getCurrentTemplate().getId();
        String templateView = getTemplateView();
        String containerId = getPara(0, scope);

        if (StrUtil.isBlank(containerId)) {
            throw new IllegalArgumentException("#blockContainer(...) argument must not be empty " + getLocation());
        }

        Columns columns = Columns.create();
        columns.eq("template_id", templateId);
        columns.eq("template_file", templateView);
        columns.eq("container_id", containerId);

        List<TemplateBlockInfo> blocks = blockInfoService.findListByColumns(columns);

        StringBuilder html = new StringBuilder();
        if (blocks != null && !blocks.isEmpty()) {
            for (TemplateBlockInfo blockInfo : blocks) {

                //设置给指令 blockOption 去使用
                scope.set("blockInfo", blockInfo);

                html.append(renderBlock(blockInfo));
            }
        }

        renderText(writer, html.toString());
    }


    private String getTemplateView() {
        Render render = JbootControllerContext.get().getRender();
        return render.getView();
    }


    private String renderBlock(TemplateBlockInfo blockInfo) {

        HttpServletRequest request = JbootControllerContext.get().getRequest();

        Map<Object, Object> data = new HashMap<>();
        for (Enumeration<String> attrs = request.getAttributeNames(); attrs.hasMoreElements(); ) {
            String attrName = attrs.nextElement();
            data.put(attrName, request.getAttribute(attrName));
        }

        String realView = TemplateManager.me().getCurrentTemplate().buildRelativePath(blockInfo.getView());

        com.jfinal.template.Template template = null;
        try {
            template = getEngine().getTemplate(realView);

            MixedByteArrayOutputStream baos = new MixedByteArrayOutputStream();
            template.render(data, baos);

            TemplateRender render = (TemplateRender) JbootControllerContext.get().getRender();
            return render.buildNormalHtml(baos.getInputStream());
        } catch (Exception ex) {
            if (ex.getMessage().contains("File not found")) {
                LogKit.error(ex.toString(), ex);
                return "";
            } else {
                throw new RuntimeException(ex);
            }
        }
    }


    private static Engine engine;

    private Engine getEngine() {
        if (engine == null) {
            engine = RenderManager.me().getEngine();
        }
        return engine;
    }
}

