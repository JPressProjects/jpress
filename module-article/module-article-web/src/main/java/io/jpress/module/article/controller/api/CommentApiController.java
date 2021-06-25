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
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.dfa.DFAUtil;
import io.jpress.model.User;
import io.jpress.module.article.kit.ArticleNotifyKit;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/api/article/comment")
public class CommentApiController extends ApiControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCommentService commentService;

    @Inject
    private OptionService optionService;


    public void paginate(@NotNull Long articleId, @DefaultValue("1") int pageNumber, @DefaultValue("10") int pageSize) {
        Page<ArticleComment> page = commentService.paginateByArticleIdInNormal(pageNumber, pageSize, articleId);
        renderJson(Ret.ok().set("page", page));
    }


    /**
     * 发布评论
     */
    public void post(@NotNull Long articleId, Long pid) {
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

        if (DFAUtil.isContainsSensitiveWords(content)) {
            renderJson(Ret.fail().set("message", "非法内容，无法发布评论信息"));
            return;
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
        boolean commentEnable = JPressOptions.isTrueOrEmpty("article_comment_enable");
        if (!commentEnable) {
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
