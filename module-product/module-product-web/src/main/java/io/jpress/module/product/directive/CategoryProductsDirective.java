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
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("categoryProducts")
public class CategoryProductsDirective extends JbootDirectiveBase {

    @Inject
    private ProductService service;

    @Inject
    private ProductCategoryService categoryService;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Long categoryId = getParaToLong("categoryId", scope);
        String flag = getPara("categoryFlag", scope);

        if (StrUtil.isBlank(flag) && categoryId == null) {
            throw new IllegalArgumentException("#categoryProducts(categoryProducts=xxx，categoryId=xxx) is error, " +
                    "categoryFlag or categoryId must not be empty. " + getLocation());
        }

        Boolean hasThumbnail = getParaToBool("hasThumbnail", scope);
        String orderBy = getPara("orderBy", scope, "order_number desc,id desc");
        int count = getParaToInt("count", scope, 10);

        ProductCategory category = categoryId != null
                ? categoryService.findById(categoryId)
                : categoryService.findFirstByFlag(flag);
        if (category == null) {
            return;
        }

        scope.setLocal("category", category);

        List<Product> products = service.findListByCategoryId(category.getId(), hasThumbnail, orderBy, count);
        if (products == null || products.isEmpty()) {
            return;
        }

        scope.setLocal("products", products);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
