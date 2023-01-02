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
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.web.functions.JPressCoreFunctions;

@JFinalDirective("unescape")
public class UnescapeDirective extends JbootDirectiveBase {
    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Object param = getPara(0, scope);
        if (param == null) {
            renderText(writer, "");
        } else if (param instanceof String) {
            renderText(writer, JPressCoreFunctions.unescape((String) param));
        }else {
            renderText(writer,param.toString());
        }
    }
}
