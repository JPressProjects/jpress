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

import com.jfinal.aop.Before;
import io.jpress.JPressConsts;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.web.interceptor.TemplateInterceptor;
import io.jpress.web.interceptor.UserInterceptor;
import io.jpress.web.render.TemplateRender;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({TemplateInterceptor.class, UserInterceptor.class})
public abstract class TemplateControllerBase extends ControllerBase {


    public void render(String view) {
        render(view, null);
    }

    public void render(String view, String defaultView) {

        //如果是 / 开头的文件，就不通过模板文件去渲染。而是去根目录去查找。
        if (view != null && view.startsWith("/")) {
            super.render(view);
            return;
        }

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            renderDefault(defaultView);
            return;
        }

        //如果是手机浏览器，优先使用 h5 模板去渲染（如果模板支持h5独立模板的话）
        if (isMoblieBrowser()) {
            //把 index.html 换成 index_h5.html
            //或者 把 article_style1.html 换成 article_style1_h5.html
            view = view.replace(".", "_h5.");
        }

        //matchTemplateFile：匹配到可以用的view
        //例如 view = article_style1_h5.html ，
        //那么 会优先匹配到 article_style1_h5.html，当article_style1_h5.html不存在的时候
        //会自动去匹配 article_style1.html，article_style1.html 不存在的时候
        //会自动去匹配 article.html
        view = template.matchTemplateFile(view);
        if (view == null) {
            renderDefault(defaultView);
            return;
        }

        view = template.getWebAbsolutePath() + "/" + view;
        super.render(new TemplateRender(view));
    }


    protected void assertNotNull(Object object) {
        if (object == null) {
            renderError(404);
        }
    }

    private void renderDefault(String defaultView) {
        if (defaultView == null) {
            renderText("can not match template view to render");
            return;
        } else {
            super.render(new TemplateRender(defaultView));
        }
    }

    protected void setWebTilte(String webTitle) {
        setAttr(JPressConsts.ATTR_WEB_TITLE, webTitle);
    }

    protected void setWebSubTilte(String webSubTitle) {
        setAttr(JPressConsts.ATTR_WEB_SUBTITLE, webSubTitle);
    }

    protected void setSeoTitle(String seoTitle) {
        setAttr(JPressConsts.ATTR_SEO_TITLE, seoTitle);
    }

    protected void setSeoKeywords(String seoKeyword) {
        setAttr(JPressConsts.ATTR_SEO_KEYWORDS, seoKeyword);
    }

    protected void setSeoDescription(String seoDescription) {
        setAttr(JPressConsts.ATTR_SEO_DESCRIPTION, seoDescription);
    }


    /**
     * 在当前页面，对菜单选中进行判断
     *
     * @param checker
     */
    protected void setMenuActive(MenuActiveChecker checker) {
        List<Menu> menus = getAttr(JPressConsts.ATTR_MENUS);
        if (menus == null || menus.isEmpty()) {
            return;
        }

        setMenuActive(checker, menus);
    }


    private void setMenuActive(MenuActiveChecker checker, List<Menu> menus) {
        for (Menu menu : menus) {
            if (checker.isActive(menu)) {
                JPressConsts.doFlagModelActive(menu);
            }
            if (menu.hasChild()) {
                setMenuActive(checker, menu.getChilds());
            }
        }
    }

    public static interface MenuActiveChecker {
        public boolean isActive(Menu menu);
    }


}
