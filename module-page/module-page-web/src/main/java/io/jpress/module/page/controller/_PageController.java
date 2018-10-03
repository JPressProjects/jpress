package io.jpress.module.page.controller;

import com.google.inject.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.TemplateManager;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
import java.util.Set;

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

        String status = getPara("status");
        String title = getPara("title");

        Page<SinglePage> page =
                StrUtils.isBlank(status)
                        ? sps._paginateWithoutTrash(getPagePara(), 10, title)
                        : sps._paginateByStatus(getPagePara(), 10, title, status);

        setAttr("page", page);

        int draftCount = sps.findCountByStatus(SinglePage.STATUS_DRAFT);
        int trashCount = sps.findCountByStatus(SinglePage.STATUS_TRASH);
        int normalCount = sps.findCountByStatus(SinglePage.STATUS_NORMAL);

        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);

        render("page/page_list.html");
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

        List<String> styles = TemplateManager.me().getCurrentTemplate().getSupportStyles("page_");
        if (ArrayUtils.isNotEmpty(styles)) {
            setAttr("styles", styles);
        }

        render("page/page_write.html");
    }

    @EmptyValidate({
            @Form(name = "page.title", message = "标题不能为空"),
            @Form(name = "page.content", message = "内容不能为空")
    })
    public void doWriteSave() {
        SinglePage page = getModel(SinglePage.class, "page");
        sps.saveOrUpdate(page);
        renderJson(Ret.ok().set("id", page.getId()));
    }


    public void doDel() {
        Long id = getIdPara();
        render(sps.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    public void doDelByIds() {
        String ids = getPara("ids");
        if (StrUtils.isBlank(ids)) {
            renderJson(Ret.fail());
            return;
        }

        Set<String> idsSet = StrUtils.splitToSet(ids, ",");
        if (idsSet == null || idsSet.isEmpty()) {
            renderJson(Ret.fail());
            return;
        }
        render(sps.deleteByIds(idsSet.toArray()) ? Ret.ok() : Ret.fail());
    }


    public void doTrash() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_TRASH) ? Ret.ok() : Ret.fail());
    }

    public void doDraft() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_DRAFT) ? Ret.ok() : Ret.fail());
    }

    public void doNormal() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_NORMAL) ? Ret.ok() : Ret.fail());
    }
}
