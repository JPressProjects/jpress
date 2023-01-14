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
package io.jpress.module.article.service.search;

import com.jfinal.aop.Aop;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.NamedThreadPools;
import io.jpress.JPressOptions;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;

import java.util.concurrent.ExecutorService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 重建文章索引
 * @Package io.jpress.module.article.task
 */
public class ArticleSearchEngineIndexRebuildTask implements JPressOptions.OptionChangeListener {

    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3, "article-search-engine-rebuilder");


    @Override
    public void onChanged(Long siteId, String key, String newValue, String oldValue) {
        if ("article_search_engine".equals(key)) {
            fixedThreadPool.execute(() -> {
                ArticleService articleService = Aop.get(ArticleService.class);
                int page = 1;
                int pagesize = 100;
                Page<Article> articlePage = articleService._paginateByColumns(page, pagesize
                        , Columns.create("status",Article.STATUS_NORMAL),null);
                do {
                    for (Article article : articlePage.getList()) {
                        ArticleSearcherFactory.getSearcher().updateArticle(article);
                    }
                    page++;
                } while (!articlePage.isLastPage());
            });
        }
    }
}
