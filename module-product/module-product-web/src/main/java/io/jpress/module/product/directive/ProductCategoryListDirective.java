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
package io.jpress.module.product.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressActiveKit;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author anjie 安杰 QQ489879492
 * @version V1.0
 * @Title: 商品多级分类
 */
@JFinalDirective("productCategoryList")
public class ProductCategoryListDirective extends JbootDirectiveBase {

    @Inject
    private ProductCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("flag", scope);
        String pflag = getPara("parentFlag", scope);
        Long pId = getParaToLong("parentId", scope);
        boolean asTree = getParaToBool("asTree", scope, Boolean.FALSE);

        List<ProductCategory> categories = categoryService.findListByType(ProductCategory.TYPE_CATEGORY);
        if (categories == null || categories.isEmpty()) {
            return;
        }

        SortKit.toLayer(categories);
        SortKit.fillParentAndChild(categories);


        if (StrUtil.isNotBlank(pflag)) {
            categories = categories.stream().filter(category -> {
                ProductCategory parent = (ProductCategory) category.getParent();
                return parent != null && pflag.equals(parent.getFlag());
            }).collect(Collectors.toList());
        }

        if (pId != null) {
            categories = categories.stream().filter(category -> {
                ProductCategory parent = (ProductCategory) category.getParent();
                return parent != null && pId.equals(parent.getId());
            }).collect(Collectors.toList());
        }


        setActiveFlagByCurrentCategory(categories);
        setActiveFlagByCurrentProduct(categories);

        if (asTree == true) {
            SortKit.toTree(categories);
        }

        if (StrUtil.isNotBlank(flag)) {
            categories = categories
                    .stream()
                    .filter(category -> flag.equals(category.getFlag()))
                    .collect(Collectors.toList());
        }

        if (categories == null || categories.isEmpty()) {
            return;
        }

        scope.setLocal("categories", categories);
        renderBody(env, scope, writer);
    }


    /**
     * 根据当前的分类，设置 高亮 标识
     *
     * @param categories
     */
    private void setActiveFlagByCurrentCategory(List<ProductCategory> categories) {

         Object data = JbootControllerContext.get().getAttr("category");
         if (data != null && data instanceof ProductCategory){
             doFlagByCurrentCategory(categories, (ProductCategory)data);
         }

    }


    /**
     * 根据当前的产品，设置 高亮 标识
     *
     * @param categories
     */
    private void setActiveFlagByCurrentProduct(List<ProductCategory> categories) {
        Product currentProduct = JbootControllerContext.get().getAttr("product");

        //当前页面并不是产品详情页面
        if (currentProduct == null) {
            return;
        }

        List<ProductCategory> productCategories = categoryService.findCategoryListByProductId(currentProduct.getId());
        if (productCategories == null || productCategories.isEmpty()) {
            return;
        }

        for (ProductCategory productCategory : productCategories) {
            doFlagByCurrentCategory(categories, productCategory);
        }
    }


    private void doFlagByCurrentCategory(List<ProductCategory> categories, ProductCategory currentCategory) {
        for (ProductCategory category : categories) {
            if (currentCategory.getId().equals(category.getId())) {
                JPressActiveKit.makeItActive(category);
            }
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
