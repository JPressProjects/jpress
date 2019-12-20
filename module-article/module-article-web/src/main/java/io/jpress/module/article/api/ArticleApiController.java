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
package io.jpress.module.article.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.layer.SortKit;
import io.jpress.model.User;
import io.jpress.module.article.kit.ArticleNotifyKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;

import java.util.List;
import java.util.stream.Collectors;

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

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;

    @Inject
    private UserService userService;

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
        String slug = getPara("slug");

        Article article = id != null ? articleService.findById(id)
                : (StrUtil.isNotBlank(slug) ? articleService.findFirstBySlug(slug) : null);

        if (article == null || !article.isNormal()) {
            renderFailJson();
            return;
        }

        articleService.doIncArticleViewCount(article.getId());
        renderJson(Ret.ok("article", article));
    }

    /**
     * 获取文章的分类
     */
    public void categories() {
        String type = getPara("type");

        if (StrUtil.isBlank(type)
                || StrUtil.isBlank(type)) {
            renderFailJson();
            return;
        }

        Long pid = getLong("pid");
        List<ArticleCategory> categories = categoryService.findListByType(type);

        if (pid != null) {
            categories = categories.stream()
                    .filter(category -> pid.equals(category.getPid()))
                    .collect(Collectors.toList());
        } else {
            SortKit.toTree(categories);
        }

        renderJson(Ret.ok("categories", categories));
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

        if (StrUtil.isBlank(slug)
                || StrUtil.isBlank(type)) {
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
        String orderBy = getPara("orderBy");
        int pageNumber = getParaToInt("pageNumber", 1);

        Page<Article> page = categoryId == null
                ? articleService.paginateInNormal(pageNumber, 10, orderBy)
                : articleService.paginateByCategoryIdInNormal(pageNumber, 10, categoryId, orderBy);

        renderJson(Ret.ok().set("page", page));
    }


    public void tagArticles() {
        String tag = getPara("tag");
        int count = getParaToInt("count", 10);
        if (StrUtil.isBlank(tag)) {
            renderFailJson();
            return;
        }

        ArticleCategory category = categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_TAG, tag);
        if (category == null) {
            renderFailJson();
            return;
        }

        List<Article> articles = articleService.findListByCategoryId(category.getId(), null, "id desc", count);
        renderJson(Ret.ok().set("articles", articles));
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

        Long id = getParaToLong("articleId");
        if (id == null) {
            renderFailJson();
        }

        int count = getParaToInt("count", 3);

        List<Article> relevantArticles = articleService.findRelevantListByArticleId(id, Article.STATUS_NORMAL, count);
        renderOkJson("articles", relevantArticles);
    }


    public void save() {
        Article article = getRawObject(Article.class);
        articleService.saveOrUpdate(article);
        renderOkJson();
    }


    public void commentPaginate() {
        Long articleId = getParaToLong("articleId");
        int pageNumber = getParaToInt("page", 1);

        Page<ArticleComment> page = commentService.paginateByArticleIdInNormal(pageNumber, 10, articleId);
        renderJson(Ret.ok().set("page", page));
    }


    /**
     * 发布评论
     */
    public void postComment() {
        Long articleId = getParaToLong("articleId");
        Long pid = getParaToLong("pid");
        String content = getRawData();

        if (articleId == null || articleId <= 0) {
            renderFailJson();
            return;
        }

        if (StrUtil.isBlank(content)) {
            renderJson(Ret.fail().set("message", "评论内容不能为空"));
            return;
        } else {
            content = StrUtil.escapeHtml(content);
        }


        Article article = articleService.findById(articleId);
        if (article == null) {
            renderFailJson();
            return;
        }

        // 文章关闭了评论的功能
        if (!article.isCommentEnable()) {
            renderJson(Ret.fail().set("message", "该文章的评论功能已关闭"));
            return;
        }

        //是否开启评论功能
        Boolean commentEnable = JPressOptions.isTrueOrEmpty("article_comment_enable");
        if (commentEnable == null || commentEnable == false) {
            renderJson(Ret.fail().set("message", "评论功能已关闭"));
            return;
        }

        User user = getLoginedUser();
        if (user == null) {
            renderJson(Ret.fail().set("message", "用户未登录"));
            return;
        }

        ArticleComment comment = new ArticleComment();

        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setPid(pid);
        comment.setEmail(user.getEmail());

        comment.setUserId(user.getId());
        comment.setAuthor(user.getNickname());

        comment.put("user", user.keepSafe());

        //是否是管理员必须审核
        Boolean reviewEnable = optionService.findAsBoolByKey("article_comment_review_enable");
        if (reviewEnable != null && reviewEnable == true) {
            comment.setStatus(ArticleComment.STATUS_UNAUDITED);
        }
        /**
         * 无需管理员审核、直接发布
         */
        else {
            comment.setStatus(ArticleComment.STATUS_NORMAL);
        }

        //记录文章的评论量
        articleService.doIncArticleCommentCount(articleId);

        if (pid != null) {
            //记录评论的回复数量
            commentService.doIncCommentReplyCount(pid);
        }
        commentService.saveOrUpdate(comment);

        Ret ret = Ret.ok();
        if (comment.isNormal()) {
            ret.set("comment", comment).set("code", 0);
        } else {
            ret.set("code", 0);
        }


        renderJson(ret);

        ArticleNotifyKit.notify(article, comment, user);
    }


}
