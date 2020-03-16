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
package io.jpress.module.product.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;


@RequestMapping(value = "/admin/product/tag", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ProductTagController extends AdminControllerBase {

    @Inject
    private ProductCategoryService productCategoryService;

    @Inject
    private MenuService menuService;

    @AdminMenu(text = "标签", groupId = "product", order = 3)
    public void index() {
        Page<ProductCategory> page = productCategoryService.paginateByType(getPagePara(), 10, ProductCategory.TYPE_TAG);
        setAttr("page", page);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            setAttr("category", productCategoryService.findById(id));
            setAttr("isDisplayInMenu", menuService.findFirstByRelatives("article_category", id) != null);
        }

        initStylesAttr("prolist_");
        render("product/product_tag_list.html");
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


    public void doSave() {

        ProductCategory tag = getModel(ProductCategory.class, "category");

        String slug = tag.getTitle().contains(".")
                ? tag.getTitle().replace(".", "_")
                : tag.getTitle();

        //新增 tag
        if (tag.getId() == null) {
            ProductCategory indbTag = productCategoryService.findFirstByTypeAndSlug(ProductCategory.TYPE_TAG, slug);
            if (indbTag != null) {
                renderJson(Ret.fail().set("message", "该标签已经存在，不能新增。"));
                return;
            }
        }

        tag.setSlug(slug);
        saveCategory(tag);
    }


    private void saveCategory(ProductCategory category) {
        if (!validateSlug(category)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }

        Object id = productCategoryService.saveOrUpdate(category);
//        productCategoryService.updateCount(category.getId());

        Menu displayMenu = menuService.findFirstByRelatives("product_category", id);
        Boolean isDisplayInMenu = getParaToBoolean("displayInMenu");
        if (isDisplayInMenu != null && isDisplayInMenu) {
            if (displayMenu == null) {
                displayMenu = new Menu();
            }

            displayMenu.setUrl(category.getUrl());
            displayMenu.setText(category.getTitle());
            displayMenu.setType(Menu.TYPE_MAIN);
            displayMenu.setOrderNumber(category.getOrderNumber());
            displayMenu.setRelativeTable("product_category");
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


    public void doDel() {
        Long id = getIdPara();
        render(productCategoryService.deleteById(id) ? Ret.ok() : Ret.fail());
    }
}