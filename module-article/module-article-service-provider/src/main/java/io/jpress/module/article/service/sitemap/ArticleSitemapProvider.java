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
package io.jpress.module.article.service.sitemap;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class ArticleSitemapProvider implements SitemapProvider {

    private String name;
    private int page;
    private Date lastmod;

    @Inject
    private ArticleService articleService;


    public ArticleSitemapProvider() {
    }

    public ArticleSitemapProvider(String name, int page) {
        this.name = name;
        this.page = page;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getLastmod() {
        if (lastmod != null) {
            return lastmod;
        }

        lastmod = Db.queryDate("select `modified` from article  where status = 'normal' order by id desc limit " + ((page - 1) * 100) + ",1");
        return lastmod;
    }


    @Override
    public List<Sitemap> getSitemaps() {

        Page<Article> articlePage = articleService.paginateInNormal(page, 100);
        if (articlePage.getList().isEmpty()) {
            return null;
        }

        return articlePage.getList().stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }
}
