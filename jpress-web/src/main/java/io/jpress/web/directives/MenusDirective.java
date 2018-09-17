package io.jpress.web.directives;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StringUtils;
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

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String type = getParam(0, scope);

        if (StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("#menus(type) argument must not be empty");
        }

        List<Menu> menus = menuService.findListByType(type);
        MenuKits.toTreeCategories(menus);

        scope.setLocal("menus", menus);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}

