package io.jpress.web.base;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
public abstract class UcenterControllerBase extends UserControllerBase {


    @Override
    public void render(String view) {
        if (view.startsWith("/")) {
            super.render(view);
        } else {
            super.render("/WEB-INF/views/ucenter/" + view);
        }
    }


    public int getPagePara() {
        return getParaToInt("page", 1);
    }


}
