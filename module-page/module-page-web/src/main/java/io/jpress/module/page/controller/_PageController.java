/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
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
    private SinglePageCategoryService categoryService;

    @Inject
    private MenuService menuService;

    @AdminMenu(text = "页面管理", groupId = "page", order = 1)
    public void list() {

        String status = getPara("status");
        String title = getPara("title");
        Long categoryId = getParaToLong("categoryId");

        Columns col= Columns.create();
        col.likeAppendPercent("title",title);
        col.eq("category_id",categoryId);

        Page<SinglePage> page =
                StrUtil.isBlank(status)
                        ? sps._paginateWithoutTrashAndColumns(getPagePara(), 10, col)
                        : sps._paginateByColumns(getPagePara(), 10, col.eq("status",status));

        if(page != null){
            for (SinglePage singlePage : page.getList()) {
                if(singlePage.getCategoryId() != null){
                    SinglePageCategory category = categoryService.findById(singlePage.getCategoryId());
                    singlePage.put("category",category);
                }
            }
        }

        setAttr("page", page);

        int draftCount = sps.findCountByStatus(SinglePage.STATUS_DRAFT);
        int trashCount = sps.findCountByStatus(SinglePage.STATUS_TRASH);
        int normalCount = sps.findCountByStatus(SinglePage.STATUS_NORMAL);

        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);

        List<SinglePageCategory> categories = categoryService.findAll();
        setAttr("categories", categories);

        render("page/page_list.html");
    }

    @AdminMenu(text = "新建", groupId = "page", order = 2)
    public void write() {

        List<SinglePageCategory> categories = categoryService.findAll();
        setAttr("categories", categories);

        int pageId = getParaToInt(0, 0);

        SinglePage page = pageId > 0 ? sps.findById(pageId) : null;
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
        Long oldCategoryId = getParaToLong("oldCategoryId");

        //默认情况下，请求会被 escape，通过 getOriginalPara 获得非 escape 的数据
        page.setContent(getCleanedOriginalPara("page.content"));

        Ret validRet = validateSlug(page);
        if (validRet.isFail()) {
            renderJson(validRet);
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

        if(page.getCategoryId() != null){
            //更新该分类的内容数量
            categoryService.doUpdatePageCategoryCount(page.getCategoryId());

            //编辑页面时选择了其他的页面分类，也要更新掉之前分类下的内容数量 (count-1)
            if(oldCategoryId != null && !page.getCategoryId().equals(oldCategoryId)){
                categoryService.doUpdatePageCategoryCount(oldCategoryId);
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


    @AdminMenu(text = "分组", groupId = "page", order = 9)
    public void category() {
        List<SinglePageCategory> categories = categoryService.findAll();
        setAttr("categories", categories);
        long id = getParaToLong(0, 0L);
        if (id > 0 && categories != null) {
            for (SinglePageCategory category : categories) {
                if (category.getId().equals(id)) {
                    setAttr("category", category);
                }
            }
        }
        initStylesAttr("page_");
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



    @EmptyValidate({
            @Form(name = "category.title", message = "分类名称不能为空")
    })
    public void doCategorySave() {
        SinglePageCategory category = getModel(SinglePageCategory.class, "category");

        categoryService.saveOrUpdate(category);
        categoryService.doUpdatePageCategoryCount(category.getId());
        renderOkJson();
    }

    public void doCategoryDel() {
        categoryService.deleteById(getIdPara());
        renderOkJson();
    }





}
