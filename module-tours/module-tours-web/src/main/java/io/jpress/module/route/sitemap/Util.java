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
package io.jpress.module.route.sitemap;


import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.route.model.TRoute;
import io.jpress.web.sitemap.Sitemap;

class Util {

    public static Sitemap toSitemap(TRoute route) {
        Sitemap sitemap = new Sitemap();
        sitemap.setChangefreq(Sitemap.CHANGEFREQ_WEEKLY);
        sitemap.setLoc(route.getUrl());
        sitemap.setLastmod(route.getModified() != null ? route.getModified() : route.getCreated());
        sitemap.setPriority(0.5f);
        return sitemap;
    }

}
