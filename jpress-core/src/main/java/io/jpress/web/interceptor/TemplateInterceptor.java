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
package io.jpress.web.interceptor;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class TemplateInterceptor implements Interceptor {


    @Override
    public void intercept(Invocation inv) {

        Controller controller = inv.getController();

        setGlobalAttrs(controller.getRequest());


        //添加CSRF的配置，方便在前台进行退出等操作
        String csrfToken = CSRFInterceptor.createSCRFToken(inv);
        if (csrfToken != null) {
            inv.getController().setCookie(CSRFInterceptor.CSRF_KEY, csrfToken, -1);
            inv.getController().setAttr(CSRFInterceptor.CSRF_ATTR_KEY, csrfToken);
        }

        inv.invoke();
    }


    public static void setGlobalAttrs(HttpServletRequest request){

        request.setAttribute(JPressConsts.ATTR_WEB_TITLE, JPressOptions.get(JPressConsts.OPTION_WEB_TITLE, "JPress"));
        request.setAttribute(JPressConsts.ATTR_WEB_SUBTITLE, JPressOptions.get(JPressConsts.OPTION_WEB_SUBTITLE, "欢迎使用JPress"));
        request.setAttribute(JPressConsts.ATTR_WEB_NAME, JPressOptions.get(JPressConsts.OPTION_WEB_NAME));
        request.setAttribute(JPressConsts.ATTR_WEB_IPC_NO, JPressOptions.get(JPressConsts.OPTION_WEB_IPC_NO));
        request.setAttribute(JPressConsts.ATTR_SEO_TITLE, JPressOptions.get(JPressConsts.OPTION_SEO_TITLE));
        request.setAttribute(JPressConsts.ATTR_SEO_KEYWORDS, JPressOptions.get(JPressConsts.OPTION_SEO_KEYWORDS));
        request.setAttribute(JPressConsts.ATTR_SEO_DESCRIPTION, JPressOptions.get(JPressConsts.OPTION_SEO_DESCRIPTION));

        request.setAttribute(JPressConsts.ATTR_WEB_DOMAIN, JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN));
        request.setAttribute(JPressConsts.ATTR_WEB_COPYRIGHT, JPressOptions.get(JPressConsts.OPTION_WEB_COPYRIGHT));


        MenuService menuService = Aop.get(MenuService.class);
        List<Menu> menus = menuService.findListByType(Menu.TYPE_MAIN);
        SortKit.toTree(menus);
        request.setAttribute(JPressConsts.ATTR_MENUS, menus);


        Template template = TemplateManager.me().getCurrentTemplate();
        request.setAttribute("TPATH", template == null ? "" : template.getRelativePath());
    }


}
