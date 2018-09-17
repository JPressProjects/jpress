package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import io.jboot.web.controller.JbootController;
import io.jpress.JPressConstants;
import io.jpress.model.User;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({AdminInterceptor.class, UserInterceptor.class, PermissionInterceptor.class})
public abstract class AdminControllerBase extends JbootController {

    @Override
    public void render(String view) {
        if (view.startsWith("/")) {
            super.render(view);
        } else {
            super.render("/WEB-INF/views/admin/" + view);
        }
    }

    public void render(Ret ret) {
        renderJson(ret);
    }

    @Before(NotAction.class)
    public User getLoginedUser() {
        return getAttr(JPressConstants.ATTR_LOGINED_USER);
    }

    /**
     * 获得当前页面的页码
     *
     * @return
     */
    public int getPagePara() {
        return getParaToInt("page", 1);
    }

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

    protected boolean validateSlug(Model model) {
        String slug = (String) model.get("slug");
        return slug == null ? true : !slug.contains("-");
    }


}
