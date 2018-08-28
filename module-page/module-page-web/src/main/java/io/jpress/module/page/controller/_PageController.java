package io.jpress.module.page.controller;

import com.google.inject.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping("/admin/page")
public class _PageController extends AdminControllerBase {

    @Inject
    private SinglePageService sps;

    @AdminMenu(text = "页面管理", groupId = "page")
    public void index() {

        Page<SinglePage> page = sps.paginate(getParaToInt("page", 1), 10);
        setAttr("page", page);

        render("page/list.html");
    }

    @AdminMenu(text = "新建", groupId = "page")
    public void write() {
        int pageId = getParaToInt(0, 0);

        if (pageId > 0) {
            SinglePage page = sps.findById(pageId);
            if (page == null) {
                renderError(404);
                return;
            }
            setAttr("page", page);
        }

        render("page/write.html");
    }

    @EmptyValidate({
            @Form(name = "page.title", message = "标题不能为空"),
            @Form(name = "page.text", message = "内容不能为空")
    })
    public void doWriteSave() {
        SinglePage page = getModel(SinglePage.class, "page");
        sps.saveOrUpdate(page);
        renderJson(Ret.ok().set("id", page.getId()));
    }
}
