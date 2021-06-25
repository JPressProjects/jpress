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
package io.jpress.module.product.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/30
 */
@RequestMapping("/api/product/category")
public class ProductCategoryApiController extends ApiControllerBase {

    @Inject
    private ProductCategoryService productCategoryService;

    /**
     * 获取商品详情
     */
    public Ret detail(Long id, String slug) {

        return Ret.ok();//.set("product", product);
    }


    /**
     * 创建新的产品
     *
     * @param productCategory
     * @return
     */
    public Ret create(@JsonBody @NotNull ProductCategory productCategory) {
        productCategoryService.save(productCategory);
        return Rets.OK;
    }


    /**
     * 更新产品
     *
     * @param productCategory
     * @return
     */
    public Ret update(@JsonBody @NotNull ProductCategory productCategory) {
        productCategoryService.update(productCategory);
        return Rets.OK;
    }


}
