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
package io.jpress.web.interceptor;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 */
public class TemplateInterceptor implements Interceptor, JPressOptions.OptionChangeListener {


    private static String webTitle = null;
    private static String webSubTitle = null;
    private static String webName = null;
    private static String webDomain = null;
    private static String webCopyright = null;
    private static String webIpcNo = null;
    private static String seoTitle = null;
    private static String seoKeyword = null;
    private static String seoDescription = null;

    public TemplateInterceptor() {
        JPressOptions.addListener(this);
    }

    public static void init() {

        webTitle = JPressOptions.get(JPressConsts.OPTION_WEB_TITLE);
        webSubTitle = JPressOptions.get(JPressConsts.OPTION_WEB_SUBTITLE);
        webName = JPressOptions.get(JPressConsts.OPTION_WEB_NAME);
        webDomain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN);
        webCopyright = JPressOptions.get(JPressConsts.OPTION_WEB_COPYRIGHT);
        webIpcNo = JPressOptions.get(JPressConsts.OPTION_WEB_IPC_NO);
        seoTitle = JPressOptions.get(JPressConsts.OPTION_SEO_TITLE);
        seoKeyword = JPressOptions.get(JPressConsts.OPTION_SEO_KEYWORDS);
        seoDescription = JPressOptions.get(JPressConsts.OPTION_SEO_DESCRIPTION);

    }


    @Override
    public void intercept(Invocation inv) {

        Controller controller = inv.getController();

        controller.setAttr(JPressConsts.ATTR_WEB_TITLE, StrUtil.escapeHtml(webTitle));
        controller.setAttr(JPressConsts.ATTR_WEB_SUBTITLE, StrUtil.escapeHtml(webSubTitle));
        controller.setAttr(JPressConsts.ATTR_WEB_NAME, StrUtil.escapeHtml(webName));
        controller.setAttr(JPressConsts.ATTR_WEB_IPC_NO, StrUtil.escapeHtml(webIpcNo));
        controller.setAttr(JPressConsts.ATTR_SEO_TITLE, StrUtil.escapeHtml(seoTitle));
        controller.setAttr(JPressConsts.ATTR_SEO_KEYWORDS, StrUtil.escapeHtml(seoKeyword));
        controller.setAttr(JPressConsts.ATTR_SEO_DESCRIPTION, StrUtil.escapeHtml(seoDescription));

        controller.setAttr(JPressConsts.ATTR_WEB_DOMAIN, webDomain);
        controller.setAttr(JPressConsts.ATTR_WEB_COPYRIGHT, webCopyright);

        //添加CSRF的配置，方便在前台进行退出等操作
        String uuid = StrUtil.uuid();
        inv.getController().setCookie(CSRFInterceptor.CSRF_KEY, uuid, -1);
        inv.getController().setAttr(CSRFInterceptor.CSRF_ATTR_KEY, uuid);

        MenuService menuService = Aop.get(MenuService.class);
        List<Menu> menus = menuService.findListByType(Menu.TYPE_MAIN);
        SortKit.toTree(menus);
        controller.setAttr(JPressConsts.ATTR_MENUS, menus);


        Template template = TemplateManager.me().getCurrentTemplate();
        controller.setAttr("TPATH", template == null ? "" : template.getRelativePath());

        inv.invoke();
    }


    @Override
    public void onChanged(String key, String newValue, String oldValue) {

        switch (key) {
            case JPressConsts.OPTION_WEB_TITLE:
                webTitle = newValue;
                break;
            case JPressConsts.OPTION_WEB_SUBTITLE:
                webSubTitle = newValue;
                break;
            case JPressConsts.OPTION_WEB_NAME:
                webName = newValue;
                break;
            case JPressConsts.OPTION_WEB_COPYRIGHT:
                webCopyright = newValue;
                break;
            case JPressConsts.OPTION_WEB_IPC_NO:
                webIpcNo = newValue;
                break;
            case JPressConsts.OPTION_WEB_DOMAIN:
                webDomain = newValue;
                break;
            case JPressConsts.OPTION_SEO_TITLE:
                seoTitle = newValue;
                break;
            case JPressConsts.OPTION_SEO_KEYWORDS:
                seoKeyword = newValue;
                break;
            case JPressConsts.OPTION_SEO_DESCRIPTION:
                seoDescription = newValue;
                break;
        }

    }
}
