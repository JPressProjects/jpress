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
package io.jpress.module.article.sitemap;

import io.jpress.module.article.model.Article;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 备注：ArticlesSitemapProvider 必须为抽象类，否则会被自动加载
 */
public class ArticlesSitemapProvider implements SitemapProvider {

    private String name;
    private List<Article> articleList;

    public ArticlesSitemapProvider() {
    }

    public ArticlesSitemapProvider(String name, List<Article> articleList) {
        this.name = name;
        this.articleList = articleList;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Date getLastmod() {
        List<Sitemap> sitemaps = getSitemaps();
        return sitemaps == null || sitemaps.isEmpty() ? null : sitemaps.get(0).getLastmod();
    }


    @Override
    public List<Sitemap> getSitemaps() {
        if (articleList == null || articleList.isEmpty()) {
            return null;
        }
        return articleList.stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }
}
