package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.User;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;
import org.apache.commons.lang.StringEscapeUtils;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/article")
public class ArticleController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;

    public void index() {
        Article article = getArticle();
        assertNotNull(article);

        setSeoTitle(article.getTitle());
        setSeoDescription(maxLength(article.getContent(), 100));

        Long page = getParaToLong(1);

        setAttr("article", article);
        render(article.getHtmlView());
    }

    private String maxLength(String content, int maxLength) {
        if (StrUtils.isBlank(content)) {
            return content;
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("#maxLength(content,length) 参数错误，length必须大于0 ");
        }

        return content.length() <= maxLength ? content :
                content.substring(0, maxLength);

    }


    private Article getArticle() {
        String idOrSlug = getPara(0);
        return StrUtils.isNumeric(idOrSlug)
                ? articleService.findById(idOrSlug)
                : articleService.findFirstBySlug(idOrSlug);
    }


    /**
     * 发布评论
     */
    public void postComment() {
        Long articleId = getParaToLong("articleId");
        Long pid = getParaToLong("pid");
        String nickname = getPara("nickname");
        String content = getPara("content");
        String email = getPara("email");
        String wechat = getPara("wechat");
        String qq = getPara("qq");

        if (articleId == null || articleId <= 0) {
            renderJson(Ret.fail());
            return;
        }

        if (StrUtils.isBlank(content)) {
            renderJson(Ret.fail());
            return;
        } else {
            content = StringEscapeUtils.escapeHtml(content);
        }

        Article article = articleService.findById(articleId);
        if (article == null) {
            renderJson(Ret.fail());
            return;
        }

        // 文章关闭了评论的功能
        if (!article.isCommentEnable()) {
            renderJson(Ret.fail().set("message", "该文章的评论功能已关闭"));
            return;
        }

        //是否开启评论功能
        Boolean commentEnable = optionService.findAsBoolByKey("article_comment_enable");
        if (commentEnable == null || commentEnable == false) {
            renderJson(Ret.fail().set("message", "评论功能已关闭"));
            return;
        }

        //是否对用户输入验证码进行验证
        Boolean vCodeEnable = optionService.findAsBoolByKey("article_comment_vcode_enable");
        if (vCodeEnable != null && vCodeEnable == true) {
            if (validateCaptcha("captcha") == false) {
                renderJson(Ret.fail().set("message", "验证码错误"));
                return;
            }
        }

        //是否允许未登录用户参与评论
        Boolean unLoginEnable = optionService.findAsBoolByKey("article_comment_unlogin_enable");
        if (unLoginEnable != null && unLoginEnable == true) {
            if (getLoginedUser() == null) {
                renderJson(Ret.fail().set("message", "未登录用户不能评论"));
                return;
            }
        }

        ArticleComment comment = new ArticleComment();

        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setNickname(nickname);
        comment.setPid(pid);
        comment.setEmail(email);
        comment.setWechat(wechat);
        comment.setQq(qq);

        User user = getLoginedUser();
        if (user != null) {
            comment.setUserId(user.getId());
            comment.setNickname(user.getNickname());
            comment.setAvatar(user.getAvatar());
        }

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


        commentService.saveOrUpdate(comment);
        renderJson(Ret.ok());
    }


}
