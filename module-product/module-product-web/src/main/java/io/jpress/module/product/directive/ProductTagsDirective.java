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
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 某个产品的标签
 */
@JFinalDirective("productTags")
public class ProductTagsDirective extends JbootDirectiveBase {

    @Inject
    private ProductCategoryService categoryService;
    

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Long id = getParaToLong(0, scope);

        if (id == null) {
            throw new IllegalArgumentException("#productTags(id) args error. id must not be null." + getLocation());
        }


        List<ProductCategory> categories = categoryService.findListByProductId(id, ProductCategory.TYPE_TAG);
        if (categories == null || categories.isEmpty()) {
            return;
        }

        scope.setLocal("tags", categories);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
