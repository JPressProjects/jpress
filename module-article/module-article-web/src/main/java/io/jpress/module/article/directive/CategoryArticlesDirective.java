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
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.commons.service.SiteModelProxy;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("categoryArticles")
public class CategoryArticlesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;

    @Inject
    private ArticleCategoryService categoryService;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Long categoryId = getParaToLong("categoryId", scope);
        String flag = getPara("categoryFlag", scope);

        if (StrUtil.isBlank(flag) && categoryId == null) {
            throw new IllegalArgumentException("#categoryArticles(categoryFlag=xxx，categoryId=xxx) is error, " +
                    "categoryFlag or categoryId must not be empty. " + getLocation());
        }

        Boolean hasThumbnail = getParaToBool("hasThumbnail", scope);
        String orderBy = getPara("orderBy", scope, "order_number desc,id desc");
        int count = getParaToInt("count", scope, 10);
        Boolean withRecommend = getParaToBool("withRecommend", scope);
        Boolean withTop = getParaToBool("withTop", scope);
        Boolean withHot = getParaToBool("withHot", scope);
        Boolean withLeadNews = getParaToBool("withLeadNews", scope);
        boolean includeChildren = getParaToBool("includeChildren", scope, true);

        ArticleCategory category = categoryId != null
                ? categoryService.findById(categoryId)
                : categoryService.findFirstByFlag(flag);
        if (category == null) {
            return;
        }

        scope.setLocal("category", category);

        Columns columns = Columns.create();
        columns.eq("with_recommend", withRecommend);
        columns.eq("with_top", withTop);
        columns.eq("with_hot", withHot);
        columns.eq("with_lead_news", withLeadNews);
        columns.isNotNullIf("article.thumbnail", hasThumbnail != null && hasThumbnail)
                .isNullIf("article.thumbnail", hasThumbnail != null && !hasThumbnail);


        try {
            //分类的内容，可以直接读取跨站内容
            SiteModelProxy.useAllSites();
            List<Article> articles = service.findListByCategoryId(category.getId(), includeChildren, columns, orderBy, count);
            if (articles != null && !articles.isEmpty()) {
                scope.setLocal("articles", articles);
                renderBody(env, scope, writer);
            }
        } finally {
            SiteModelProxy.clearUsed();
        }

    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
