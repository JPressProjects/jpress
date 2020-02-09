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
package io.jpress.module.product.api;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.base.ApiControllerBase;

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
    public void index(){
        Long id = getParaToLong("id");
        String slug = getPara("slug");

        Product product = id != null ? productService.findById(id)
                : (StrUtil.isNotBlank(slug) ? productService.findFirstBySlug(slug) : null);

        if (product == null || !product.isNormal()) {
            renderFailJson();
            return;
        }

        productService.doIncProductViewCount(product.getId());
        renderOkJson("product", product);
    }



    /**
     * 获取产品列表
     */
    public void list(){
        String flag = getPara("flag");
        Boolean hasThumbnail = getParaToBoolean("hasThumbnail");
        String orderBy = getPara("orderBy", "id desc");
        int count = getParaToInt("count", 10);


        Columns columns = Columns.create("flag", flag);
        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("thumbnail");
            } else {
                columns.isNull("thumbnail");
            }
        }

        List<Product> products = productService.findListByColumns(columns, orderBy, count);
        renderOkJson("products", products);
    }


    /**
     * 某个商品的相关商品
     */
    public void relevantList() {

        Long id = getParaToLong("productId");
        if (id == null) {
            renderFailJson();
        }

        int count = getParaToInt("count", 3);

        List<Product> relevantProducts = productService.findRelevantListByProductId(id, Product.STATUS_NORMAL, count);
        renderOkJson("products", relevantProducts);
    }


    /**
     * 商品搜索
     */
    public void productSearch(String keyword){
        int page = getParaToInt("page",1);
        int pageSize = getParaToInt("size",10);
        Page<Product> dataPage = StrUtil.isNotBlank(keyword)
                ? productService.search(keyword, page, pageSize)
                : null;
        renderJson(dataPage);
    }



}
