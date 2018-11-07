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
package io.jpress.web.base;

import com.jfinal.core.NotAction;
import io.jboot.web.controller.JbootController;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.model.User;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
public abstract class ControllerBase extends JbootController {


    @NotAction
    public Long getIdPara() {
        Long id = getParaToLong();
        if (id == null) {

            //renderError 会直接抛出异常，阻止程序往下执行
            renderError(404);
        }

        return id;
    }


    protected void assertNotNull(Object object) {
        if (object == null) {
            renderError(404);
        }
    }


    @Override
    @NotAction
    public String getPara(String name) {
        String value = super.getPara(name);
        return "".equals(value) ? null : value;
    }


    protected User getLoginedUser() {
        return getAttr(JPressConsts.ATTR_LOGINED_USER);
    }

    public String getEscapeHtmlPara(String name) {
        String value = super.getPara(name);
        if (value == null || "".equals(value)) {
            return null;
        }
        return CommonsUtils.escapeHtml(value);
    }

}
