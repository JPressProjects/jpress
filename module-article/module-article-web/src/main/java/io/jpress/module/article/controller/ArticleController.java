package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.base.UserInterceptor;

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

        Long page = getParaToLong(1);

        setAttr("article", article);
        render(article.getHtmlView());
    }


    private Article getArticle() {
        String idOrSlug = getPara(0);
        return StrUtils.isNumeric(idOrSlug)
                ? articleService.findById(idOrSlug)
                : articleService.findFirstBySlug(idOrSlug);
    }


    public void category() {
        ArticleCategory category = getArticleCategory(ArticleCategory.TYPE_CATEGORY);
        assertNotNull(category);
        setAttr("category", category);
        render(category.getHtmlView());
    }


    public void tag() {
        ArticleCategory category = getArticleCategory(ArticleCategory.TYPE_TAG);
        assertNotNull(category);
        setAttr("category", category);
        render(category.getHtmlView());
    }


    private ArticleCategory getArticleCategory(String type) {
        String idOrSlug = getPara(0);
        return StrUtils.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(type, idOrSlug);

    }


    /**
     * 发布评论
     */
    public void postComment() {
        Long articleId = getParaToLong("articleId");
        Long pid = getParaToLong("pid");
        String author = getPara("author");
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
        }

        Article article = articleService.findById(articleId);
        if (article == null) {
            renderJson(Ret.fail());
            return;
        }

        // 文章关闭了评论的功能
        if (!article.isCommentEnable()) {
            renderJson(Ret.fail());
            return;
        }

        //是否开启评论功能
        Boolean commentEnable = optionService.findAsBoolByKey("article_comment_enable");
        if (commentEnable == null || commentEnable == false) {
            renderJson(Ret.fail());
            return;
        }

        //是否对用户输入验证码进行验证
        Boolean vCodeEnable = optionService.findAsBoolByKey("article_comment_vcode_enable");
        if (vCodeEnable != null || vCodeEnable == true) {
            if (doValidateCommentVCode() == false) {
                renderJson(Ret.fail());
                return;
            }
        }

        //是否允许未登录用户参与评论
        Boolean unLoginEnable = optionService.findAsBoolByKey("article_comment_unlogin_enable");
        if (unLoginEnable != null || unLoginEnable == true) {
            if (doValidateUserLogined() == false) {
                renderJson(Ret.fail());
                return;
            }
        }

        ArticleComment comment = new ArticleComment();

        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setPid(pid);
        comment.setArticleUserId(article.getUserId());
        comment.setEmail(email);
        comment.setWechat(wechat);
        comment.setQq(qq);

        //是否是管理员必须审核
        Boolean reviewEnable = optionService.findAsBoolByKey("article_comment_review_enable");
        if (unLoginEnable != null || unLoginEnable == true) {
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

    /**
     * 验证用户是否已经登录
     *
     * @return 用户已经登录：true ，否则：false
     */
    private boolean doValidateUserLogined() {
        return UserInterceptor.getThreadLocalUser() != null;
    }

    /**
     * 对验证码进行验证
     *
     * @return 用户输入验证码正确：true , 否则：false
     */
    private boolean doValidateCommentVCode() {
        return validateCaptcha("captcha");
    }


}
