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
package io.jpress.module.article.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.apidoc.annotation.ApiResp;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章相关的 API
 */
@RequestMapping("/api/article")
@Api("文章相关API文档")
public class ArticleApiController extends ApiControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;


    @ApiOper(value = "文章详情", paraNotes = "id 或者 slug 必须传入一个值")
    @ApiResp(field = "detail", dataType = Article.class, notes = "文章详情")
    public Ret detail(@ApiPara("文章ID") Long id, @ApiPara("文章固定连接") String slug) {

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
     * 通过 分类ID 分页读取文章列表
     */
    @ApiOper("文章分页读取")
    @ApiResp(field = "page", notes = "文章分页数据", dataType = Page.class, genericTypes = Article.class)
    public Ret paginate(@ApiPara("文章分类ID") Long categoryId
            , @ApiPara("排序方式") String orderBy
            , @ApiPara("分页的页码") @DefaultValue("1") int pageNumber
            , @ApiPara("每页的数据数量") @DefaultValue("10") int pageSize) {
        Page<Article> page = categoryId == null
                ? articleService.paginateInNormal(pageNumber, pageSize, orderBy)
                : articleService.paginateByCategoryIdInNormal(pageNumber, pageSize, categoryId, false, orderBy);

        return Ret.ok().set("page", page);
    }


    /**
     * 通过文章分类的 ID 操作文章列表
     *
     * @param categoryId
     * @param count
     * @return
     */
    @ApiOper("根据文章分的ID查找文章列表")
    @ApiResp(field = "list", notes = "文章列表", dataType = List.class, genericTypes = Article.class)
    public Ret listByCategoryId(@ApiPara("文章分类ID") @NotNull Long categoryId
            , @ApiPara("查询数量") @DefaultValue("10") int count) {
        List<Article> articles = articleService.findListByCategoryId(categoryId, true,null, "id desc", count);
        return Ret.ok().set("list", articles);
    }


    /**
     * 通过文章分类的固定链接查找所有文章列表
     *
     * @param categorySlug
     * @param count
     * @return
     */
    @ApiOper("根据文章分类的固定连接查找文章列表")
    @ApiResp(field = "list", notes = "文章列表", dataType = List.class, genericTypes = Article.class)
    public Ret listByCategorySlug(@ApiPara("分类的固定连接") @NotEmpty String categorySlug
            , @ApiPara("查询数量") @DefaultValue("10") int count) {
        ArticleCategory category = categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_TAG, categorySlug);
        if (category == null) {
            return Rets.FAIL;
        }

        List<Article> articles = articleService.findListByCategoryId(category.getId(), true,null, "id desc", count);
        return Ret.ok().set("list", articles);
    }


    /**
     * 通过 文章属性 获得文章列表
     */
    @ApiOper("根据文章的 flag 查找文章列表")
    @ApiResp(field = "list", notes = "文章列表", dataType = List.class, genericTypes = Article.class)
    public Ret listByFlag(@ApiPara("文章标识") @NotEmpty String flag
            , @ApiPara(value = "是否必须要图片", notes = "true 必须有图片，false 必须无图片") Boolean hasThumbnail
            , @ApiPara("排序方式") String orderBy
            , @ApiPara("查询数量") @DefaultValue("10") int count) {

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


    @ApiOper("查询某一篇文章的 相关文章")
    @ApiResp(field = "list", notes = "文章列表", dataType = List.class, genericTypes = Article.class)
    public Ret listByRelevant(@ApiPara("文章ID") @NotNull Long articleId
            , @ApiPara("查询数量") @DefaultValue("3") int count) {
        List<Article> relevantArticles = articleService.findRelevantListByArticleId(articleId, Article.STATUS_NORMAL, count);
        return Ret.ok().set("list", relevantArticles);
    }


    @ApiOper("文章搜索")
    @ApiResp(field = "page", notes = "文章分页数据", dataType = Page.class, genericTypes = Article.class)
    public Ret search(@ApiPara("关键词") String keyword
            , @ApiPara("分页页码") @DefaultValue("1") int pageNumber
            , @ApiPara("每页数量") @DefaultValue("10") int pageSize) {
        Page<Article> dataPage = StrUtil.isNotBlank(keyword)
                ? articleService.search(keyword, pageNumber, pageSize)
                : null;
        return Ret.ok().set("page", dataPage);
    }


    @ApiOper("删除文章")
    public Ret doDelete(@ApiPara("文章ID") @NotNull Long id) {
        articleService.deleteById(id);
        return Rets.OK;
    }

    @ApiOper(value = "创建新文章", contentType = ContentType.JSON)
    @ApiResp(field = "id", notes = "文章ID", dataType = Long.class, mock = "123")
    public Ret doCreate(@ApiPara("文章的 JSON 信息") @JsonBody Article article) {
        Object id = articleService.save(article);
        return Ret.ok().set("id", id);
    }

    @ApiOper(value = "更新文章", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("文章的 JSON 信息") @JsonBody Article article) {
        articleService.update(article);
        return Rets.OK;
    }


}
