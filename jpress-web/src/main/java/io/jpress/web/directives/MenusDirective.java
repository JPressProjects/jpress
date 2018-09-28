package io.jpress.web.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;
import io.jpress.web.commons.kits.MenuKits;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.directives
 */
@JFinalDirective("menus")
public class MenusDirective extends JbootDirectiveBase {

    @Inject
    private MenuService menuService;

//    @Inject
//    private OptionService optionService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String type = getParam(0, scope);

        if (StrUtils.isBlank(type)) {
            throw new IllegalArgumentException("#menus(type) argument must not be empty");
        }

        List<Menu> menus = menuService.findListByType(type);
        if (ArrayUtils.isNullOrEmpty(menus)) {
            return;
        }

//        Boolean enable = optionService.findAsBoolByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_ENABLE);
//        if (enable == null || enable == false) {
//            renderMenu(env, scope, writer, menus);
//            return;
//        }
//
//        String suffix = optionService.findByKey(JPressConstants.OPTION_WEB_FAKE_STATIC_SUFFIX);
//        if (StringUtils.isBlank(suffix)) {
//            renderMenu(env, scope, writer, menus);
//            return;
//        }
//
//        for (AdminMenu menu : menus) {
//            if (StringUtils.isBlank(menu.getUrl())) {
//                continue;
//            }
//
//            if (menu.getUrl().startsWith("/") && !menu.getUrl().endsWith(suffix)) {
//                menu.setUrl(menu.getUrl() + suffix);
//            }
//        }

        renderMenu(env, scope, writer, menus);
    }

    private void renderMenu(Env env, Scope scope, Writer writer, List<Menu> menus) {
        MenuKits.toTreeCategories(menus);
        scope.setLocal("menus", menus);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}

