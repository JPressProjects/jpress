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

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.Article;

public class NoneSearcher implements ArticleSearcher {

    @Override
    public void addArticle(Article article) {

    }

    @Override
    public void deleteArticle(Object id) {

    }

    @Override
    public void updateArticle(Article article) {

    }

    @Override
    public Page<Article> search(String keyword, int pageNum, int pageSize) {
        return null;
    }
}
