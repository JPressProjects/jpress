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
package io.jpress.module.article.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressActiveKit;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章分类：分类、专题、标签等
 */
@JFinalDirective("categories")
public class CategoriesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("flag", scope);
        String pflag = getPara("parentFlag", scope);
        Long pId = getParaToLong("parentId", scope);
        boolean asTree = getParaToBool("asTree", scope, Boolean.FALSE);

        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        if (categories == null || categories.isEmpty()) {
            return;
        }

        SortKit.toLayer(categories);
        SortKit.fillParentAndChild(categories);


        if (StrUtil.isNotBlank(pflag)) {
            categories = categories.stream().filter(category -> {
                ArticleCategory parent = (ArticleCategory) category.getParent();
                return parent != null && pflag.equals(parent.getFlag());
            }).collect(Collectors.toList());
        }

        if (pId != null) {
            categories = categories.stream().filter(category -> {
                ArticleCategory parent = (ArticleCategory) category.getParent();
                return parent != null && pId.equals(parent.getId());
            }).collect(Collectors.toList());
        }


        setActiveFlagByCurrentCategory(categories);
        setActiveFlagByCurrentArticle(categories);

        if (asTree == true) {
            SortKit.toTree(categories);
        }

        if (StrUtil.isNotBlank(flag)) {
            categories = categories
                    .stream()
                    .filter(category -> flag.equals(category.getFlag()))
                    .collect(Collectors.toList());
        }

        if (categories == null || categories.isEmpty()) {
            return;
        }

        scope.setLocal("categories", categories);
        renderBody(env, scope, writer);
    }


    /**
     * 根据当前的分类，设置 高亮 标识
     *
     * @param categories
     */
    private void setActiveFlagByCurrentCategory(List<ArticleCategory> categories) {

        Object data = JbootControllerContext.get().getAttr("category");

        if (data != null && data instanceof ArticleCategory) {
            doFlagByCurrentCategory(categories, (ArticleCategory) data);
        }

    }


    /**
     * 根据当前的文章，设置 高亮 标识
     *
     * @param categories
     */
    private void setActiveFlagByCurrentArticle(List<ArticleCategory> categories) {
        Article currentArticle = JbootControllerContext.get().getAttr("article");

        //当前页面并不是文章详情页面
        if (currentArticle == null) {
            return;
        }

        List<ArticleCategory> articleCategories = categoryService.findListByArticleId(currentArticle.getId());
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
                JPressActiveKit.makeItActive(category);
            }
        }
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
