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
package io.jpress.module.article.controller.api;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.ApiControllerBase;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/api/article")
public class ArticleApiController extends ApiControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    /**
     * 文章详情的api
     * 可以传 id 获取文章详情，也可以通过 slug 来获取文章详情
     * 例如：
     * http://127.0.0.1:8080/api/article?id=123
     * 或者
     * http://127.0.0.1:8080/api/article?slug=myslug
     */
    public void index() {
        Long id = getParaToLong("id");
        if (id != null) {
            Article article = articleService.findById(id);
            renderJson(Ret.ok("article", article));
            return;
        }

        String slug = getPara("slug");
        if (slug != null) {
            Article article = articleService.findFirstBySlug(slug);
            renderJson(Ret.ok("article", article));
            return;
        }

        renderFailJson();
    }


    /**
     * 获取分类详情的API
     * <p>
     * 可以通过 id 获取文章分类，也可以通过 type + slug 定位到分类
     * 分类可能是后台对应的分类，有可以是一个tag（tag也是一种分类）
     * <p>
     * 例如：
     * http://127.0.0.1:8080/api/article/category?id=123
     * 或者
     * http://127.0.0.1:8080/api/article/category?type=category&slug=myslug
     * http://127.0.0.1:8080/api/article/category?type=tag&slug=myslug
     */
    public void category() {
        Long id = getParaToLong("id");
        if (id != null) {
            ArticleCategory category = categoryService.findById(id);
            renderJson(Ret.ok("category", category));
            return;
        }

        String slug = getPara("slug");
        String type = getPara("type");

        if (StrUtils.isBlank(slug)
                || StrUtils.isBlank(type)) {
            renderFailJson();
            return;
        }


        ArticleCategory category = categoryService.findFirstByTypeAndSlug(type, slug);
        renderJson(Ret.ok("category", category));
    }


    /**
     * 通过 分类ID 分页读取文章列表
     */
    public void paginate() {
        Long categoryId = getParaToLong("categoryId");
        if (categoryId == null || categoryId <= 0) {
            renderFailJson();
            return;
        }

        String orderBy = getPara("orderBy");
        int pageNumber = getParaToInt("page", 1);

        Page<Article> page = articleService.paginateByCategoryIdInNormal(pageNumber, 10, categoryId, orderBy);
        renderJson(Ret.ok().set("page", page));

    }


    /**
     * 通过 文章属性 获得文章列表
     */
    public void list() {
        String flag = getPara("flag");
        Boolean hasThumbnail = getParaToBoolean("hasThumbnail");
        String orderBy = getPara("orderBy", "id desc");
        int count = getParaToInt("count", 10);


        Columns columns = Columns.create("flag", flag);
        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.is_not_null("thumbnail");
            } else {
                columns.is_null("thumbnail");
            }
        }

        List<Article> articles = articleService.findListByColumns(columns, orderBy, count);
        renderJson(Ret.ok("articles", articles));
    }

    /**
     * 某一篇文章的相关文章
     */
    public void relevantList() {

        Long id = getParaToLong("id");
        if (id == null) {
            renderFailJson();
        }

        int count = getParaToInt("count", 3);

        List<Article> relevantArticles = articleService.findRelevantListByArticleId(id, Article.STATUS_NORMAL, count);
        renderOk("articles", relevantArticles);
    }


    public void save() {
        Article article = getRawObject(Article.class);
        articleService.saveOrUpdate(article);

        renderJson(Ret.ok());
    }


}
