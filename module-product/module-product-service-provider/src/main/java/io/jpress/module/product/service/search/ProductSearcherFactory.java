/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.core.spi.JbootSpiLoader;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.module.product.search.ElasticSearcher;
import io.jpress.module.product.searcher.DbSearcher;
import io.jpress.module.product.searcher.LuceneSearcher;

public class ProductSearcherFactory {

    public static ProductSearcher getSearcher() {
        ProductSearcher searcher = null;
        try {
            searcher = tryGetSearcher();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return searcher == null ? new NoneSearcher() : searcher;
    }


    private static ProductSearcher tryGetSearcher() {

        boolean searchEnable = JPressOptions.isTrueOrEmpty("product_search_enable");
        if (!searchEnable) {
            return null;
        }

        String engine = JPressOptions.get("product_search_engine");
        if (StrUtil.isBlank(engine)) {
            return Aop.get(DbSearcher.class);
        }

        switch (engine) {
            case "sql":
                return Aop.get(DbSearcher.class);
            case "lucene":
                return Aop.get(LuceneSearcher.class);
            case "es":
                return Aop.get(ElasticSearcher.class);
            default:
        }

        ProductSearcher searcher = JbootSpiLoader.load(ProductSearcher.class, engine);
        return searcher != null ? Aop.inject(searcher) : Aop.get(DbSearcher.class);

    }
}
