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
package io.jpress.module.article.service.sitemap;

import com.jfinal.aop.Inject;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.web.sitemap.Sitemap;
import io.jpress.web.sitemap.SitemapProvider;

import java.util.List;
import java.util.stream.Collectors;


public class ArticleCategorySitemapProvider implements SitemapProvider {

    @Inject
    private ArticleCategoryService categoryService;


    @Override
    public List<Sitemap> getSitemaps() {
        List<ArticleCategory> tagList = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        if (tagList == null || tagList.isEmpty()) {
            return null;
        }
        return tagList.stream()
                .map(Util::toSitemap)
                .collect(Collectors.toList());
    }
}
