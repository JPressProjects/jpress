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
package io.jpress.web.directive;

import com.jfinal.template.Env;
import com.jfinal.template.TemplateException;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressOptions;

import java.io.IOException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("option")
public class OptionDirective extends JbootDirectiveBase {


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String key = getPara(0, scope);
        if (StrUtil.isBlank(key)) {
            throw new IllegalArgumentException("#option(...) argument must not be empty " + getLocation());
        }

        String defaultValue = getPara(1, scope, "");

        Long siteId = getParaToLong(2, scope);

        String value = siteId != null ? JPressOptions.getBySiteId(key, siteId) : JPressOptions.get(key);
        if (value == null || "".equals(value)) {
            value = defaultValue;
        }

        try {
            writer.write(value);
        } catch (IOException e) {
            throw new TemplateException(e.getMessage(), location, e);
        }
    }
}

