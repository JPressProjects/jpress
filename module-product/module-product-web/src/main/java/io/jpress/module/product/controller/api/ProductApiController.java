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
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/30
 */
@RequestMapping("/api/product")
public class ProductApiController extends ApiControllerBase {

    @Inject
    private ProductService productService;

    /**
     * 获取商品详情
     */
    public Ret detail(Long id, String slug) {
        Product product = id != null ? productService.findById(id)
                : (StrUtil.isNotBlank(slug) ? productService.findFirstBySlug(slug) : null);

        if (product == null || !product.isNormal()) {
            return Rets.FAIL;
        }

        productService.doIncProductViewCount(product.getId());
        return Ret.ok().set("product", product);
    }


    /**
     * 根据产品的 Flag 获取产品列表
     */
    public Ret productsByFlag(String flag, Boolean hasThumbnail, String orderBy, @DefaultValue("10") int count) {

        Columns columns = Columns.create("flag", flag);
        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("thumbnail");
            } else {
                columns.isNull("thumbnail");
            }
        }

        List<Product> products = productService.findListByColumns(columns, orderBy, count);
        return Ret.ok().set("products", products);
    }


    /**
     * 某个商品的相关商品
     */
    public Ret productsByRelevant(@NotNull @Min(1) Long id, @DefaultValue("3") int count) {
        List<Product> relevantProducts = productService.findRelevantListByProductId(id, Product.STATUS_NORMAL, count);
        return Ret.ok().set("products", relevantProducts);
    }


    /**
     * 商品搜索
     */
    public Ret search(String keyword, @DefaultValue("1") int pageNumber, @DefaultValue("10") int pageSize) {
        Page<Product> dataPage = StrUtil.isNotBlank(keyword)
                ? productService.search(keyword, pageNumber, pageSize)
                : null;
        return Ret.ok().set("page", dataPage);
    }



    /**
     * 创建新的产品
     * @param article
     * @return
     */
    public Ret create(@JsonBody @NotNull Product article) {
        productService.save(article);
        return Rets.OK;
    }


    /**
     * 更新产品
     * @param article
     * @return
     */
    public Ret update(@JsonBody @NotNull Product article) {
        productService.update(article);
        return Rets.OK;
    }


}
