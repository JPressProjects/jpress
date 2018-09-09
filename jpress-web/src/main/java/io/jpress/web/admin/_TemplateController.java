package io.jpress.web.admin;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConstants;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.service.OptionService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.model.Menu;
import io.jpress.model.Role;
import io.jpress.service.MenuService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.commons.kits.MenuKits;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/admin/template")
public class _TemplateController extends AdminControllerBase {

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private MenuService ms;

    @Inject
    private OptionService optionService;

    @AdminMenu(text = "所有模板", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 0)
    public void index() {
        List<Template> templates = TemplateManager.me().getInstalledTemplates();
        setAttr("templates", templates);

        render("template/list.html");
    }


    @AdminMenu(text = "安装", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 5)
    public void install() {
        render("template/install.html");
    }


    public void enable() {
        String tid = getPara("tid");
        Template template = TemplateManager.me().getTemplateById(tid);

        if (template == null) {
            renderJson(Ret.fail().set("message", "没有干模板"));
            return;
        }

        optionService.saveOrUpdate("web_template", template.getId());
        renderJson(Ret.ok());
    }

    public void roleSave() {
        Role role = getModel(Role.class);
        roleService.saveOrUpdate(role);
        redirect("/admin/user/role");
    }


    @AdminMenu(text = "设置", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 88)
    public void me() {
        render("user/me.html");
    }

    @AdminMenu(text = "编辑", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 99)
    public void edit() {
        render("template/edit.html");
    }


    @AdminMenu(text = "菜单", groupId = JPressConstants.SYSTEM_MENU_TEMPLATE, order = 6)
    public void menu() {
        List<Menu> menus = ms.findListByType(Menu.TYPE_MAIN);
        MenuKits.toLayerCategories(menus);
        setAttr("menus", menus);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (Menu menu : menus) {
                if (menu.getId() == id) {
                    setAttr("menu", menu);
                }
            }
        }

        render("template/menu.html");
    }

    public void doMenuSave() {
        Menu category = getModel(Menu.class);
        ms.saveOrUpdate(category);
        redirect("/admin/template/menu");
    }

    public void doMenuDel() {
        int id = getParaToInt(0, 0);
        if (id == 0) {
            renderError(404);
            return;
        }

        ms.deleteById(id);
        renderJson(Ret.ok());
    }


}
