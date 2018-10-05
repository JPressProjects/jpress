package io.jpress.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jpress.JPressConstants;
import io.jpress.model.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Api的拦截器
 * @Package io.jpress.web
 */
public class TemplateInterceptor implements Interceptor {


    private static String webTitle = null;
    private static String webSubTitle = null;
    private static String webName = null;
    private static String webDomain = null;
    private static String webCopyright = null;
    private static String seoTitle = null;
    private static String seoKeyword = null;
    private static String seoDescription = null;

    private static List<Menu> menus = null;

    public static void setWebTitle(String webTitle) {
        TemplateInterceptor.webTitle = webTitle;
    }

    public static void setWebSubTitle(String webSubTitle) {
        TemplateInterceptor.webSubTitle = webSubTitle;
    }

    public static void setWebName(String webName) {
        TemplateInterceptor.webName = webName;
    }

    public static void setWebDomain(String webDomain) {
        TemplateInterceptor.webDomain = webDomain;
    }

    public static void setWebCopyright(String webCopyright) {
        TemplateInterceptor.webCopyright = webCopyright;
    }

    public static void setSeoTitle(String seoTitle) {
        TemplateInterceptor.seoTitle = seoTitle;
    }

    public static void setSeoKeyword(String seoKeyword) {
        TemplateInterceptor.seoKeyword = seoKeyword;
    }

    public static void setSeoDescription(String seoDescription) {
        TemplateInterceptor.seoDescription = seoDescription;
    }

    public static void setMenus(List<Menu> menus) {
        TemplateInterceptor.menus = menus;
    }

    public void intercept(Invocation inv) {

        Controller controller = inv.getController();

        controller.setAttr(JPressConstants.ATTR_WEB_TITLE, webTitle);
        controller.setAttr(JPressConstants.ATTR_WEB_SUBTITLE, webSubTitle);
        controller.setAttr(JPressConstants.ATTR_WEB_NAME, webName);
        controller.setAttr(JPressConstants.ATTR_WEB_DOMAIN, webDomain);
        controller.setAttr(JPressConstants.ATTR_WEB_COPYRIGHT, webCopyright);
        controller.setAttr(JPressConstants.ATTR_SEO_TITLE, seoTitle);
        controller.setAttr(JPressConstants.ATTR_SEO_KEYWORDS, seoKeyword);
        controller.setAttr(JPressConstants.ATTR_SEO_DESCRIPTION, seoDescription);

        if (menus != null) {
            /**
             * 为什么每次要用一个新的list ？
             * 因为：不同的页面，会改变不同的 menu 属性
             */
            controller.setAttr(JPressConstants.ATTR_MENUS, new ArrayList<>(menus));
        }

        inv.invoke();
    }

}
