package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({AdminInterceptor.class, UserInterceptor.class, PermissionInterceptor.class})
public abstract class AdminControllerBase extends ControllerBase {

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

    /**
     * 获得当前页面的页码
     *
     * @return
     */
    public int getPagePara() {
        return getParaToInt("page", 1);
    }



    protected boolean validateSlug(Model model) {
        String slug = (String) model.get("slug");
        return slug == null ? true : !slug.contains("-");
    }


}
