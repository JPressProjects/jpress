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
package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.utils.StrUtil;
import io.jpress.web.interceptor.AdminInterceptor;
import io.jpress.web.interceptor.CSRFInterceptor;
import io.jpress.web.interceptor.PermissionInterceptor;
import io.jpress.web.interceptor.UserInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({
        CSRFInterceptor.class,
        AdminInterceptor.class,
        UserInterceptor.class,
        PermissionInterceptor.class
})
public abstract class AdminControllerBase extends ControllerBase {

    @NotAction
    public void render(Ret ret) {
        renderJson(ret);
    }

    /**
     * 获得当前页面的页码
     *
     * @return
     */
    @NotAction
    public int getPagePara() {
        return getParaToInt("page", 1);
    }


    protected boolean validateSlug(Model model) {
        String slug = (String) model.get("slug");
        return slug == null ? true : !slug.contains("-") && !StrUtil.isNumeric(slug);
    }


    private static final String NO_PERMISSION_VIEW = "/WEB-INF/views/admin/error/nopermission.html";

    @NotAction
    public void renderErrorForNoPermission() {
        if (isAjaxRequest()) {
            renderJson(Ret.fail().set("message", "您没有权限操作此功能。"));
        } else {
            render(NO_PERMISSION_VIEW);
        }
    }

    @Override
    public void renderError(int errorCode) {
        if (errorCode == 404) {
            renderError(errorCode, "/WEB-INF/views/admin/error/404.html");
        } else {
            renderError(errorCode, "/WEB-INF/views/admin/error/500.html");
        }
    }


}
