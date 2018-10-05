package io.jpress.web.front;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.model.Menu;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.handler.JPressHandler;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/")
public class IndexController extends TemplateControllerBase {


    public void index() {

        if ("/".equals(JPressHandler.getCurrentTarget())) {
            doFlagMenuActive();
            render("index.html");
            return;
        }


        forwardAction("/page");
    }

    private void doFlagMenuActive() {
        List<Menu> menus = getMenus();
//        menus.stream()
//                .filter(menu -> "/".equals(menu.getUrl()))
//                .map(menu -> menu.put(JPressConstants.IS_ACTIVE, true));

        for (Menu menu : menus){
            if ("/".equals(menu.getUrl())){
                menu.put(JPressConstants.IS_ACTIVE,true);
            }
        }
    }


}
