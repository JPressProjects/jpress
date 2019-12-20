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
package io.jpress.module.article.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.CommonsUtils;
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
import io.jpress.web.base.TemplateControllerBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/article")
public class ArticleController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private UserService userService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;


    public void index() {
        Article article = getArticle();

        //当文章处于审核中、草稿等的时候，显示404
        render404If(article == null || !article.isNormal());


        if (StrUtil.isNotBlank(article.getLinkTo())) {
            redirect(article.getLinkTo());
            return;
        }

        //设置页面的seo信息
        setSeoInfos(article);


        //设置菜单高亮
        doFlagMenuActive(article);

        //记录当前浏览量
        articleService.doIncArticleViewCount(article.getId());

        User articleAuthor = article.getUserId() != null
                ? userService.findById(article.getUserId())
                : null;

        article.put("user", articleAuthor);

        setAttr("article", article);

        render(article.getHtmlView());
    }

    private void setSeoInfos(Article article) {
        setSeoTitle(article.getTitle());
        setSeoKeywords(article.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(article.getMetaDescription())
                ? CommonsUtils.maxLength(article.getText(), 100)
                : article.getMetaDescription());
    }


    private Article getArticle() {
        String idOrSlug = getPara(0);
        return StrUtil.isNumeric(idOrSlug)
                ? articleService.findById(idOrSlug)
                : articleService.findFirstBySlug(StrUtil.urlDecode(idOrSlug));
    }


    private void doFlagMenuActive(Article article) {

        setMenuActive(menu -> menu.isUrlStartWidth(article.getUrl()));

        List<ArticleCategory> articleCategories = categoryService.findCategoryListByArticleId(article.getId());
        if (articleCategories == null || articleCategories.isEmpty()) {
            return;
        }

        setMenuActive(menu -> {
            if ("article_category".equals(menu.getRelativeTable())) {
                for (ArticleCategory category : articleCategories) {
                    if (category.getId().equals(menu.getRelativeId())) {
                        return true;
                    }
                }
            }
            return false;
        });

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
            renderFailJson();
            return;
        }

        if (StrUtil.isBlank(content)) {
            renderJson(Ret.fail().set("message", "评论内容不能为空"));
            return;
        } else {
            content = StrUtil.escapeHtml(content);
        }

        //是否对用户输入验证码进行验证
        Boolean vCodeEnable = JPressOptions.isTrueOrEmpty("article_comment_vcode_enable");
        if (vCodeEnable != null && vCodeEnable == true) {
            if (validateCaptcha("captcha") == false) {
                renderJson(Ret.fail().set("message", "验证码错误").set("errorCode",2));
                return;
            }
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


        //是否允许未登录用户参与评论
        Boolean unLoginEnable = optionService.findAsBoolByKey("article_comment_unlogin_enable");
        if (unLoginEnable == null || unLoginEnable == false) {
            if (getLoginedUser() == null) {
                renderJson(Ret.fail().set("message", "未登录用户不能评论").set("errorCode", 9));
                return;
            }
        }

        ArticleComment comment = new ArticleComment();

        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setAuthor(nickname);
        comment.setPid(pid);
        comment.setEmail(email);
        comment.setWechat(wechat);
        comment.setQq(qq);

        User user = getLoginedUser();
        if (user != null) {
            comment.setUserId(user.getId());
            comment.setAuthor(user.getNickname());
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

        //记录文章的评论量
        articleService.doIncArticleCommentCount(articleId);

        commentService.saveOrUpdate(comment);

        if (pid != null) {
            //记录评论的回复数量
            commentService.doIncCommentReplyCount(pid);

            ArticleComment parent = commentService.findById(pid);
            if (parent != null && parent.isNormal()){
                comment.put("parent",parent);
            }
        }

        Ret ret = Ret.ok().set("code",0);


        Map<String, Object> paras = new HashMap<>();
        paras.put("comment", comment);
        paras.put("article", article);
        if (user != null) {
            paras.put("user", user.keepSafe());
        }

        setRetHtml(ret,paras,"/WEB-INF/views/commons/article/defaultArticleCommentItem.html");

        ArticleNotifyKit.notify(article, comment, user);

        if (isAjaxRequest()) {
            renderJson(ret);
        } else {
            redirect(getReferer());
        }
    }


}
