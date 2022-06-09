/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.module.page.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping(value = "/admin/page", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _PageController extends AdminControllerBase {

    @Inject
    private SinglePageService sps;

    @Inject
    private SinglePageCommentService commentService;

    @Inject
    private SinglePageCategoryService categoryService;

    @Inject
    private MenuService menuService;

    @AdminMenu(text = "页面管理", groupId = "page", order = 1)
    public void list() {

        String status = getPara("status");
        String title = getPara("title");

        Page<SinglePage> page =
                StrUtil.isBlank(status)
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

    @AdminMenu(text = "新建", groupId = "page", order = 2)
    public void write() {

        List<SinglePageCategory> categories = categoryService.findListByType(SinglePageCategory.TYPE_CATEGORY);
        setAttr("categories", categories);

        int pageId = getParaToInt(0, 0);

        SinglePage page = pageId > 0 ? sps.findById(pageId) : null;
        if(page != null){
            Long[] categoryIds = categoryService.findCategoryIdsBySinglePageId(page.getId());
            flagCheck(categories, categoryIds);
        }
        setAttr("page", page);

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template != null) {
            List<String> styles = template.getSupportStyles("page_");
            setAttr("styles", styles);
            setAttr("flags", template.getFlags());
        }

        String editMode = page == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : page.getEditMode();
        setAttr("editMode", StrUtil.isBlank(editMode) ? "html" : editMode);

        render("page/page_write.html");
    }



    @AdminMenu(text = "设置", groupId = "page", order = 6)
    public void setting() {
        render("page/setting.html");
    }


    @EmptyValidate({
            @Form(name = "id", message = "页面ID不能为空"),
            @Form(name = "mode", message = "页面编辑模式不能为空")
    })
    public void doChangeEditMode() {
        Long id = getParaToLong("id");
        String mode = getPara("mode");

        SinglePage page = sps.findById(id);
        if (page == null) {
            renderFailJson();
            return;
        }

        page.setEditMode(mode);
        sps.update(page);
        renderOkJson();
    }


    @EmptyValidate({
            @Form(name = "page.title", message = "标题不能为空"),
            @Form(name = "page.content", message = "内容不能为空")
    })
    public void doWriteSave() {
        SinglePage page = getModel(SinglePage.class, "page");

        //默认情况下，请求会被 escape，通过 getOriginalPara 获得非 escape 的数据
        page.setContent(getCleanedOriginalPara("page.content"));

        if (!validateSlug(page)) {
            renderJson(Ret.fail("message", "固定连接不能以数字结尾"));
            return;
        }

        if (StrUtil.isNotBlank(page.getSlug())) {
            SinglePage exsitModel = sps.findFirstBySlug(page.getSlug());
            if (exsitModel != null && !exsitModel.getId().equals(page.getId())) {
                renderJson(Ret.fail("message", "该固定链接已经存在"));
                return;
            }
        }

        long id = (long) sps.saveOrUpdate(page);

        Long[] saveBeforeCategoryIds = null;
        if (page.getId() != null){
            saveBeforeCategoryIds = categoryService.findCategoryIdsBySinglePageId(page.getId());
        }


        Long[] categoryIds = getParaValuesToLong("category");

        Long[] updateCategoryIds = ArrayUtils.addAll(categoryIds);

        sps.doUpdateCategorys(id, updateCategoryIds);


        if (updateCategoryIds != null && updateCategoryIds.length > 0) {
            for (Long categoryId : updateCategoryIds) {
                categoryService.doUpdatePageCount(categoryId);
            }
        }

        if (saveBeforeCategoryIds != null && saveBeforeCategoryIds.length > 0) {
            for (Long categoryId : saveBeforeCategoryIds) {
                categoryService.doUpdatePageCount(categoryId);
            }
        }



        renderJson(Ret.ok().set("id", page.getId()));
    }


    public void doDel() {
        Long id = getIdPara();
        render(sps.deleteById(id) ? OK : FAIL);
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(sps.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    public void doTrash() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_TRASH) ? OK : FAIL);
    }

    public void doDraft() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_DRAFT) ? OK : FAIL);
    }

    public void doNormal() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_NORMAL) ? OK : FAIL);
    }


    @AdminMenu(text = "分类", groupId = "page", order = 2)
    public void category() {
        List<SinglePageCategory> categories = categoryService.findListByType(SinglePageCategory.TYPE_CATEGORY);
        setAttr("categories", categories);
        long id = getParaToLong(0, 0L);
        if (id > 0 && categories != null) {
            for (SinglePageCategory category : categories) {
                if (category.getId().equals(id)) {
                    setAttr("category", category);
                    setAttr("isDisplayInMenu", menuService.findFirstByRelatives("single_page_category", id) != null);
                }
            }
        }
        initStylesAttr("pagelist_");
        render("page/page_category_list.html");
    }

    private void initStylesAttr(String prefix) {
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return;
        }
        setAttr("flags", template.getFlags());
        List<String> styles = template.getSupportStyles(prefix);
        setAttr("styles", styles);
    }

    private void flagCheck(List<SinglePageCategory> categories, Long... checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (SinglePageCategory category : categories) {
            for (Long id : checkIds) {
                if (id != null && id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
    }


    @EmptyValidate({
            @Form(name = "category.title", message = "分类名称不能为空"),
            @Form(name = "category.slug", message = "slug 不能为空")
    })
    public void doCategorySave() {
        SinglePageCategory category = getModel(SinglePageCategory.class, "category");
        saveCategory(category);
    }

    private void saveCategory(SinglePageCategory category) {
        if (!validateSlug(category)) {
            renderJson(Ret.fail("message", "固定连接不能以数字结尾"));
            return;
        }

        SinglePageCategory existModel = categoryService.findFirstByTypeAndSlug(category.getType(), category.getSlug());
        if (existModel != null && !Objects.equals(existModel.getId(), category.getId())) {
            renderJson(Ret.fail("message", "该分类的固定连接以及被占用"));
            return;
        }

        Object id = categoryService.saveOrUpdate(category);
        categoryService.doUpdatePageCount(category.getId());

        Menu displayMenu = menuService.findFirstByRelatives("single_page_category", id);
        Boolean isDisplayInMenu = getParaToBoolean("displayInMenu");
        if (isDisplayInMenu != null && isDisplayInMenu) {
            if (displayMenu == null) {
                displayMenu = new Menu();
            }

            displayMenu.setUrl(category.getUrl());
            displayMenu.setText(category.getTitle());
            displayMenu.setType(Menu.TYPE_MAIN);
            displayMenu.setOrderNumber(category.getOrderNumber());
            displayMenu.setRelativeTable("single_page_category");
            displayMenu.setRelativeId((Long) id);

            if (displayMenu.getPid() == null) {
                displayMenu.setPid(0L);
            }

            if (displayMenu.getOrderNumber() == null) {
                displayMenu.setOrderNumber(99);
            }

            menuService.saveOrUpdate(displayMenu);
        } else if (displayMenu != null) {
            menuService.delete(displayMenu);
        }

        renderOkJson();
    }

    public void doCategoryDel() {
        categoryService.deleteById(getIdPara());
        renderOkJson();
    }





}
