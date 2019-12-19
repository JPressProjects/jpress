/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 */
@RequestMapping("/product/tag")
public class ProductTagController extends TemplateControllerBase {

    @Inject
    private ProductCategoryService categoryService;


    public void index() {
        if (StrUtil.isBlank(getPara())) {
            renderError(404);
            return;
        }

        ProductCategory tag = getTag();
        if (tag == null) {
            renderError(404);
            return;
        }

        setAttr("category", tag);
        setSeoInfos(tag);

        render(getRenderView(tag));
    }

    private void setSeoInfos(ProductCategory category) {
        if (category == null) {
            return;
        }

        setSeoTitle(category.getTitle());
        setSeoKeywords(category.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(category.getMetaDescription())
                ? CommonsUtils.maxLength(category.getContent(), 100)
                : category.getMetaDescription());
    }


    private ProductCategory getTag() {
        String idOrSlug = getPara(0);

        if (StrUtil.isBlank(idOrSlug)) {
            return null;
        }

        return StrUtil.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(ProductCategory.TYPE_TAG, StrUtil.urlDecode(idOrSlug));

    }

    private String getRenderView(ProductCategory category) {
        return category == null
                ? "prolist.html"
                : category.getHtmlView();
    }


}
