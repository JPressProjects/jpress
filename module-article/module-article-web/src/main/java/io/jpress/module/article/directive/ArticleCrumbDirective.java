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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@JFinalDirective("articleCrumb")
public class ArticleCrumbDirective extends JbootDirectiveBase {

    @Inject
    private ArticleCategoryService articleCategoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        //a 标签的样式
        String aClass = getParaToString("aClass", scope, "");

        //首页的链接
        String indexUrl = getParaToString("indexUrl", scope, "/");

        //首页的文字内容
        String indexText = getParaToString("indexText", scope, "首页");

        //显示的最大层级
        int maxLevelCount = getParaToInt("maxLevelCStt", scope, 5);

        //面包屑的分隔符
        String separator = getParaToString("separator", scope, " &gt; ");

        Multimap<Integer, ArticleCategory> crumbCategories = HashMultimap.create();

        List<ArticleCategory> allArticleCategories = articleCategoryService.findAll();

        //移除移除 tag 等类型
        allArticleCategories.removeIf(c -> !ArticleCategory.TYPE_CATEGORY.equals(c.getType()));


        SortKit.toLayer(allArticleCategories);

        List<ArticleCategory> currentCategories = null;

        //访问文章页时
        Article article = JbootControllerContext.get().getAttr("article");
        if (article != null) {
            currentCategories = articleCategoryService.findListByArticleId(article.getId());
        }

        //访问分类页时
        ArticleCategory currentArticleCategory = JbootControllerContext.get().getAttr("category");
        if (currentArticleCategory != null) {
            if (currentCategories == null) {
                currentCategories = new ArrayList<>();
            }
            currentCategories.add(currentArticleCategory);
        }

        buildArticleCrumbs(currentCategories, allArticleCategories, crumbCategories);

        StringBuilder crumb = new StringBuilder();
        crumb.append("<a  href=\"").append(indexUrl).append("\" class=\"").append(aClass).append("\" >").append(indexText).append("</a>");

        if (!crumbCategories.isEmpty()) {
            crumb.append(separator);
            List<Integer> keys = crumbCategories.keySet().stream().sorted(Comparator.comparingInt(o -> o)).collect(Collectors.toList());
            int i = 0;
            for (Integer key : keys) {
                Collection<ArticleCategory> articleCategories = crumbCategories.get(key);
                int j = 0;
                for (ArticleCategory articleCategory : articleCategories) {
                    crumb.append("<a href=\"").append(articleCategory.getUrl()).append("\"")
                            .append("  class=\"").append(aClass).append("\" >")
                            .append(articleCategory.getTitle())
                            .append("</a>");
                    if (++j != articleCategories.size()) {
                        crumb.append(", ");
                    }
                }

                if (maxLevelCount > 0 && i + 1 >= maxLevelCount) {
                    break;
                }

                if (++i != keys.size()) {
                    crumb.append(separator);
                }
            }
        }

        renderText(writer, crumb.toString());
    }

    private void buildArticleCrumbs(List<ArticleCategory> currentCategories, List<ArticleCategory> allArticleCategories
            , Multimap<Integer, ArticleCategory> crumbs) {

        if (currentCategories == null || currentCategories.isEmpty()) {
            return;
        }

        for (ArticleCategory currentCategory : currentCategories) {
            ArticleCategory category = getByIdFromList(currentCategory.getId(), allArticleCategories);
            do {
                if (category != null) {
                    crumbs.put(category.getLayerNumber(), category);
                    category = getByIdFromList(category.getParentId(), allArticleCategories);
                }
            } while (category != null);
        }

    }


    private ArticleCategory getByIdFromList(Long id, List<ArticleCategory> allArticleCategories) {
        if (id == null) {
            return null;
        }
        for (ArticleCategory articleCategory : allArticleCategories) {
            if (articleCategory.getId().equals(id)) {
                return articleCategory;
            }
        }
        return null;
    }


}
