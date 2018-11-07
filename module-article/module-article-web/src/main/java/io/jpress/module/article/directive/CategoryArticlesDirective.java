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
package io.jpress.module.article.directive;

import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtils;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("categoryArticles")
public class CategoryArticlesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;

    @Inject
    private ArticleCategoryService categoryService;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("categoryFlag", scope);
        if (StrUtils.isBlank(flag)) {
            throw new RuntimeException("#categoryArticles(categoryFlag=xxx) is error, categoryFlag must not be empty");
        }

        String orderBy = getPara("orderBy", scope, "id desc");
        int count = getPara("count", scope, 10);

        ArticleCategory category = categoryService.findFirstByFlag(flag);
        if (category == null) {
            return;
        }

        List<Article> articles = service.findListByCategoryId(category.getId(), orderBy, count);
        scope.setLocal("articles", articles);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
