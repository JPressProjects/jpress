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
            renderText("can not match view to render");
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
     * @param flager
     */
    protected void doFlagMenuActive(MenuActiveFlager flager) {
        List<Menu> menus = getAttr(JPressConsts.ATTR_MENUS);
        if (menus == null || menus.isEmpty()) {
            return;
        }

        doFlagMenuActive(flager, menus);
    }


    private void doFlagMenuActive(MenuActiveFlager flager, List<Menu> menus) {
        for (Menu menu : menus) {
            if (flager.flagActive(menu)) {
                JPressConsts.doFlagModelActive(menu);
            }
            if (menu.hasChild()) {
                doFlagMenuActive(flager, menu.getChilds());
            }
        }
    }

    public static interface MenuActiveFlager {
        public boolean flagActive(Menu menu);
    }


}
