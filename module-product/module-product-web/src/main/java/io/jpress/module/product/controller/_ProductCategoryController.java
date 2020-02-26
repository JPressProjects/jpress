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
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;


@RequestMapping(value = "/admin/product/category", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ProductCategoryController extends AdminControllerBase {

    @Inject
    private ProductCategoryService productCategoryService;

    @Inject
    private MenuService menuService;

    @AdminMenu(text = "分类", groupId = "product", order = 2)
    public void index() {
        List<ProductCategory> categories = productCategoryService.findListByType(ProductCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);
        int id = getParaToInt(0, 0);
        if (id > 0 && categories != null) {
            for (ProductCategory category : categories) {
                if (category.getId() == id) {
                    setAttr("category", category);
                    setAttr("isDisplayInMenu", menuService.findFirstByRelatives("product_category", id) != null);
                }
            }
        }
        initStylesAttr("prolist_");
        render("product/product_category_list.html");
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
        ProductCategory entry = getModel(ProductCategory.class, "category");
        saveCategory(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }


    private void saveCategory(ProductCategory category) {
        if (!validateSlug(category)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }

        Object id = productCategoryService.saveOrUpdate(category);
        productCategoryService.doUpdateProductCount(category.getId());

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