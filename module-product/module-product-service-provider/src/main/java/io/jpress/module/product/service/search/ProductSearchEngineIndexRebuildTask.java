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
package io.jpress.module.product.service.search;

import com.jfinal.aop.Aop;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.NamedThreadPools;
import io.jpress.JPressOptions;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;

import java.util.concurrent.ExecutorService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 重建文章索引
 * @Package io.jpress.module.article.task
 */
public class ProductSearchEngineIndexRebuildTask implements JPressOptions.OptionChangeListener {

    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3, "article-search-engine-rebuilder");


    @Override
    public void onChanged(Long siteId, String key, String newValue, String oldValue) {
        if ("product_search_engine".equals(key)) {
            fixedThreadPool.execute(() -> {
                ProductService articleService = Aop.get(ProductService.class);
                int page = 1;
                int pagesize = 100;
                Page<Product> productPage = articleService._paginateByStatus(page, pagesize, null, null, Product.STATUS_NORMAL);
                do {
                    for (Product product : productPage.getList()) {
                        ProductSearcherFactory.getSearcher().updateProduct(product);
                    }
                    page++;
                } while (!productPage.isLastPage());
            });
        }
    }
}
