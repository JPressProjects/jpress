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

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.core.template.BlockFunctions;
import io.jpress.service.TemplateBlockOptionService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 * <p>
 * #blockOption("aaaa ： component","defautValue")
 */
@JFinalDirective("blockOption")
public class BlockOptionDirective extends JbootDirectiveBase {

    @Inject
    private TemplateBlockOptionService blockOptionService;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String keyPara = getPara(0, scope);
        if (StrUtil.isBlank(keyPara)) {
            throw new IllegalArgumentException("#blockOption(...) argument must not be empty " + getLocation());
        }

        String key = BlockFunctions.getRealKey(keyPara);
        Object obj = scope.get(key);

        String defaultValue = getParaToString(1, scope, "");
        if (obj == null || (obj instanceof String && StrUtil.isBlank((String) obj))) {
            renderText(writer, defaultValue);
            return;
        }

        renderText(writer, obj.toString());
    }
}

