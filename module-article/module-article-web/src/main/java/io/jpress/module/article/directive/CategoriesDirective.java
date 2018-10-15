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
import io.jboot.web.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章分类：分类、专题、标签等
 * @Package io.jpress.module.article.directives
 */
@JFinalDirective("categories")
public class CategoriesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String type = getParam("type", ArticleCategory.TYPE_CATEGORY, scope);
        Boolean asTree = getParam("asTree", Boolean.FALSE, scope);

        List<ArticleCategory> categories = categoryService.findListByType(type);
        if (categories == null || categories.isEmpty()) {
            return;
        }

        doFlagIsActiveByCurrentCategory(categories);
        doFlagIsActiveByCurrentArticle(categories);

        if (asTree != null && asTree == true) {
            SortKit.toTree(categories);
        }

        scope.setLocal("categories", categories);
        renderBody(env, scope, writer);
    }


    private void doFlagIsActiveByCurrentCategory(List<ArticleCategory> categories) {

        ArticleCategory currentCategory = JbootControllerContext.get().getAttr("category");

        //当前页面并不是某个分类页面
        if (currentCategory == null) {
            return;
        }

        doFlagByCurrentCategory(categories, currentCategory);

    }


    private void doFlagIsActiveByCurrentArticle(List<ArticleCategory> categories) {
        Article currentArticle = JbootControllerContext.get().getAttr("article");

        //当前页面并不是文章详情页面
        if (currentArticle == null) {
            return;
        }

        List<ArticleCategory> articleCategories = categoryService.findActiveCategoryListByArticleId(currentArticle.getId());
        if (articleCategories == null || articleCategories.isEmpty()) {
            return;
        }

        for (ArticleCategory articleCategory : articleCategories) {
            doFlagByCurrentCategory(categories, articleCategory);
        }
    }


    private void doFlagByCurrentCategory(List<ArticleCategory> categories, ArticleCategory currentCategory) {
        for (ArticleCategory category : categories) {
            if (currentCategory.getId().equals(category.getId())) {
                JPressConsts.doFlagModelActive(category);
            }
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
