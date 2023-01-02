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
package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.StrUtil;
import io.jpress.JPressActiveKit;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.web.interceptor.TemplateInterceptor;
import io.jpress.web.interceptor.UserInterceptor;
import io.jpress.web.interceptor.WechatInterceptor;
import io.jpress.web.render.TemplateRender;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({WechatInterceptor.class,
        TemplateInterceptor.class,
        UserInterceptor.class})
public abstract class TemplateControllerBase extends ControllerBase {


    @Override
    @NotAction
    public void render(String view) {
        render(view, null);
    }

    @NotAction
    public void render(String view, String defaultView) {
        //如果是 / 开头的文件，就不通过模板文件去渲染。而是去根目录去查找。
        if (view != null && view.startsWith("/")) {
            super.render(view);
            return;
        }

        String paraView = getPara("v");

        String newView = StrUtil.isBlank(paraView) ? view : paraView + ".html";
        defaultView = StrUtil.isBlank(defaultView) ? view : defaultView;

        doRender(newView, defaultView);
    }

    protected void doRender(String view, String defaultView) {

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            renderDefault(defaultView);
            return;
        }

        boolean isMobile = isMobileBrowser();

        //匹配到可以用的 view
        view = template.matchView(view, isMobile);

        //匹配不到渲染的模板，尝试使用 default 去匹配
        if (view == null && defaultView != null && !defaultView.startsWith("/")) {
            view = template.matchView(defaultView, isMobile);
        }

        if (view == null) {
            renderDefault(defaultView);
        } else {
            super.render(new TemplateRender(template.buildRelativePath(view)));
        }

    }


    protected boolean hasTemplate(String view) {

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return false;
        }

        return template.matchView(view, isMobileBrowser()) != null;
    }

    @Override
    @NotAction
    public void redirect(String url) {
        if (url.contains("?") || url.endsWith("/")) {
            super.redirect(url);
        } else {
            super.redirect(url + JPressOptions.getAppUrlSuffix());
        }
    }

    private void renderDefault(String defaultView) {
        if (defaultView == null) {
            renderText("can not match template view to render");
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
    protected void setMenuActive(Function<Menu, Boolean> checker) {
        List<Menu> menus = getAttr(JPressConsts.ATTR_MENUS);
        if (menus == null || menus.isEmpty()) {
            return;
        }

        setMenuActive(checker, menus);
    }


    private void setMenuActive(Function<Menu, Boolean> checker, List<Menu> menus) {
        for (Menu menu : menus) {
            if (StrUtil.isNotBlank(menu.getUrl())) {
                Boolean apply = checker.apply(menu);
                if (apply != null && apply) {
                    JPressActiveKit.makeItActive(menu);
                }
            }
            if (menu.hasChild()) {
                setMenuActive(checker, menu.getChilds());
            }
        }
    }


    protected void renderHtmltoRet(String defaultTemplate, Map<String, Object> paras, Ret toRet) {
        String render = getPara("render", "default");

        Engine engine = RenderManager.me().getEngine();
        Template template = TemplateManager.me().getCurrentTemplate();

        if (paras != null && template != null) {
            paras.put("TPATH", template.getRelativePath());
        }

        if ("default".equals(render)) {
            String html = engine.getTemplate(defaultTemplate).renderToString(paras);
            toRet.set("html", html);
        } else {
            if (template != null) {
                String matchedHtml = template.matchView(render + ".html", isMobileBrowser());
                if (matchedHtml != null) {
                    String html = engine.getTemplate(template.buildRelativePath(matchedHtml)).renderToString(paras);
                    toRet.set("html", html);
                }
            }
        }
    }


    @NotAction
    public String getIdOrSlug() {
        String idOrSlug = getPara();
        if (StrUtil.isBlank(idOrSlug)) {
            return idOrSlug;
        }

        int indexOf = idOrSlug.lastIndexOf("-");
        if (indexOf == -1) {
            return idOrSlug;
        }

        String lastString = idOrSlug.substring(indexOf + 1);
        if (StrUtil.isNumeric(lastString)) {
            return idOrSlug.substring(0, indexOf);
        } else {
            return idOrSlug;
        }
    }

    public void setPageNumber(Integer pageNumber) {
        setAttr("__pageNumber", pageNumber);
    }


    @NotAction
    public int getPageNumber() {
        Integer pageNumber = getAttr("__pageNumber");
        if (pageNumber != null && pageNumber > 0) {
            return pageNumber;
        }

        String idOrSlug = getPara();
        if (StrUtil.isBlank(idOrSlug)) {
            return 1;
        }

        int indexOf = idOrSlug.lastIndexOf("-");
        if (indexOf == -1) {
            return 1;
        }

        String lastString = idOrSlug.substring(indexOf + 1);
        if (StrUtil.isNumeric(lastString)) {
            return Integer.parseInt(lastString);
        } else {
            return 1;
        }
    }


}
