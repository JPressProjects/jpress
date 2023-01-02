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
package io.jpress.core.template.directive;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.SiteContext;
import io.jpress.core.template.BlockManager;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.TemplateBlockOption;
import io.jpress.service.TemplateBlockOptionService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("blockContainer")
public class BlockContainerDirective extends JbootDirectiveBase {

    @Inject
    private TemplateBlockOptionService blockOptionService;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String templateId = TemplateManager.me().getCurrentTemplate().getId();
        String containerId = getParaToString(0, scope);

        if (StrUtil.isBlank(containerId)) {
            throw new IllegalArgumentException("#blockContainer(...) argument must not be empty " + getLocation());
        }

        TemplateBlockOption blockOption = blockOptionService.findById(templateId, SiteContext.getSiteId());
        if (blockOption == null) {
            renderBody(env, scope, writer);
            return;
        }

        //每个 container 容器的数据
        JSONArray componentBuilderJsonArray = blockOption.getJsonArrayByContainerId(containerId);
        if (componentBuilderJsonArray == null || componentBuilderJsonArray.isEmpty()) {
            renderBody(env, scope, writer);
            return;
        }

        String html = BlockManager.me().renderAll(componentBuilderJsonArray, null, false);
        renderText(writer, html);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}

