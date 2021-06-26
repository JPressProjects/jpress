/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章相关的 API
 */
@RequestMapping("/api/article")
public class ArticleApiController extends ApiControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private UserService userService;

    /**
     * 文章详情的api
     * 可以传 id 获取文章详情，也可以通过 slug 来获取文章详情
     * 例如：
     * http://127.0.0.1:8080/api/article/detail?id=123
     * 或者
     * http://127.0.0.1:8080/api/article/detail?slug=myslug
     */
    public Ret detail(Long id, String slug) {

        if (id == null && slug == null) {
            return Ret.fail().set("message", "id 或 slug 必须有一个值");
        }

        Article article = id != null ? articleService.findById(id) : articleService.findFirstBySlug(slug);

        if (article == null || !article.isNormal()) {
            return Ret.fail().set("message", "该文章不存在或已经下线");
        }

        articleService.doIncArticleViewCount(article.getId());
        return Ret.ok().set("detail", article);
    }

    /**
     * 获取文章的分类
     */
    public Ret categories(@NotEmpty String type, Long pid) {

        List<ArticleCategory> categories = categoryService.findListByType(type);
        if (categories == null || categories.isEmpty()) {
            return Ret.ok().set("categories", new HashMap<>());
        }

        if (pid != null) {
            categories = categories.stream()
                    .filter(category -> pid.equals(category.getPid()))
                    .collect(Collectors.toList());
        } else {
            SortKit.toTree(categories);
        }

        return Ret.ok().set("categories", categories);
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
    public Ret category(Long id, String slug, String type) {
        if (id == null && slug == null) {
            return Ret.fail().set("message", "id 或者 slug 必须有一个不能为空");
        }

        if (id != null) {
            ArticleCategory category = categoryService.findById(id);
            return Ret.ok("category", category);
        }

        ArticleCategory category = categoryService.findFirstByTypeAndSlug(type, slug);
        return Ret.ok("category", category);
    }


    /**
     * 通过 分类ID 分页读取文章列表
     */
    public Ret paginate(Long categoryId, String orderBy, @DefaultValue("1") int pageNumber, @DefaultValue("10") int pageSize) {
        Page<Article> page = categoryId == null
                ? articleService.paginateInNormal(pageNumber, pageSize, orderBy)
                : articleService.paginateByCategoryIdInNormal(pageNumber, pageSize, categoryId, orderBy);

        return Ret.ok().set("page", page);
    }


    /**
     * 通过文章分类的 ID 操作文章列表
     * @param categoryId
     * @param count
     * @return
     */
    public Ret listByCategoryId(@NotNull Long categoryId, @DefaultValue("10") int count) {
        List<Article> articles = articleService.findListByCategoryId(categoryId, null, "id desc", count);
        return Ret.ok().set("list", articles);
    }


    /**
     * 通过文章分类的固定链接查找所有文章列表
     * @param categorySlug
     * @param count
     * @return
     */
    public Ret listByCategorySlug(@NotEmpty String categorySlug, @DefaultValue("10") int count) {
        ArticleCategory category = categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_TAG, categorySlug);
        if (category == null) {
            return Rets.FAIL;
        }

        List<Article> articles = articleService.findListByCategoryId(category.getId(), null, "id desc", count);
        return Ret.ok().set("list", articles);
    }


    /**
     * 通过 文章属性 获得文章列表
     */
    public Ret listByFlag(@NotEmpty String flag, Boolean hasThumbnail, String orderBy, @DefaultValue("10") int count) {

        Columns columns = Columns.create("flag", flag);
        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("thumbnail");
            } else {
                columns.isNull("thumbnail");
            }
        }

        List<Article> articles = articleService.findListByColumns(columns, orderBy, count);
        return Ret.ok().set("list", articles);
    }

    /**
     * 某一篇文章的相关文章
     */
    public Ret listByRelevant(@NotNull Long articleId, @DefaultValue("3") int count) {
        List<Article> relevantArticles = articleService.findRelevantListByArticleId(articleId, Article.STATUS_NORMAL, count);
        return Ret.ok().set("list", relevantArticles);
    }


    /**
     * 搜索文章
     *
     * @param keyword
     */
    public Ret search(String keyword, @DefaultValue("1") int pageNumber, @DefaultValue("10") int pageSize) {
        Page<Article> dataPage = StrUtil.isNotBlank(keyword)
                ? articleService.search(keyword, pageNumber, pageSize)
                : null;
        return Ret.ok().set("page", dataPage);
    }


    /**
     * 删除文章
     */
    public Ret doDelete(@NotNull Long id) {
        articleService.deleteById(id);
        return Rets.OK;
    }

    /**
     * 创建新文章
     */
    public Ret doCreate(@JsonBody Article article) {
        Object id = articleService.save(article);
        return Ret.ok().set("id",id);
    }

    /**
     * 更新文章
     */
    public Ret doUpdate(@JsonBody Article article) {
        articleService.update(article);
        return Rets.OK;
    }


}
