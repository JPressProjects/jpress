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
package io.jpress.web.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressConsts;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("EditorContent")
public class EditorContentDirective extends JbootDirectiveBase {

    private static final String[] originalChars = {"&lt;", "&gt;"};
    private static final String[] newChars = {"&amp;lt;", "&amp;gt;"};

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String originalContent = getPara(0, scope);
        if (originalContent == null) {
            return;
        }

        String editMode = JbootControllerContext.get().getAttr("editMode");
        if (StrUtil.isNotBlank(editMode) && !JPressConsts.EDIT_MODE_HTML.equals(editMode)){
            renderText(writer,originalContent);
        }
        //ckeditor 编辑器有个bug，自动把 &lt; 转化为 < 和 把 &gt; 转化为 >
        //因此，此处需要 把 "&lt;" 替换为 "&amp;lt;" 和 把 "&gt;" 替换为 "&amp;gt;"
        //方案：http://komlenic.com/246/encoding-entities-to-work-with-ckeditor-3/
        else {
            renderText(writer, StringUtils.replaceEach(originalContent, originalChars, newChars));
        }
    }
}

