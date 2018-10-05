package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.model.User;
import io.jpress.module.article.kits.CategoryKits;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.web.base.UcenterControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/ucenter/article")
public class ArticleUCenterController extends UcenterControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;

    @UCenterMenu(text = "文章列表", groupId = "article", order = 0)
    public void index() {

        User loginedUser = getLoginedUser();
        Page<Article> page = articleService._paginateByUserId(getPagePara(), 10, loginedUser.getId());
        setAttr("page", page);

        render("article/article_list.html");
    }

    public void doDel() {

        Long id = getIdPara();
        if (id == null) {
            renderJson(Ret.fail());
            return;
        }

        Article article = articleService.findById(id);
        if (article == null) {
            renderJson(Ret.fail());
            return;
        }

        if (!article.getUserId().equals(getLoginedUser().getId())) {
            renderJson(Ret.fail());
            return;
        }

        renderJson(articleService.deleteById(id) ? Ret.ok() : Ret.fail());

    }

    @UCenterMenu(text = "投稿", groupId = "article", order = 1)
    public void write() {

        int articleId = getParaToInt(0, 0);

        if (articleId > 0) {

            Article article = articleService.findById(articleId);
            if (article == null) {
                renderError(404);
                return;
            }

            //用户投稿，不能编辑已经审核通过的文章
            if (article.isNormal()) {
                renderError(404);
                return;
            }

            setAttr("article", article);
        }


        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        CategoryKits.toLayerCategories(categories);
        setAttr("categories", categories);

        List<ArticleCategory> tags = categoryService.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
        setAttr("tags", tags);

        Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
        flagCheck(categories, categoryIds);

        render("article/article_write.html");
    }

    private void flagCheck(List<ArticleCategory> categories, Long... checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (ArticleCategory category : categories) {
            for (Long id : checkIds) {
                if (id != null && id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
    }


    @EmptyValidate({
            @Form(name = "article.title", message = "标题不能为空"),
            @Form(name = "article.content", message = "文章内容不能为空")
    })
    public void doWriteSave() {

        Article article = getModel(Article.class, "article");
        article.keep("id", "title", "content", "slug", "edit_mode", "summary", "thumbnail", "meta_keywords", "meta_description");

        if (!validateSlug(article)) {
            renderJson(Ret.fail("message", "slug不能包含该字符：- "));
            return;
        }

        article.setUserId(getLoginedUser().getId());
        article.setStatus(Article.STATUS_DRAFT);
        long id = articleService.doGetIdBySaveOrUpdateAction(article);


        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));
        Long[] allIds = ArrayUtils.addAll(categoryIds, tagIds);
        articleService.doUpdateCategorys(id, allIds);


        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }

    private boolean validateSlug(Model model) {
        String slug = (String) model.get("slug");
        return slug == null ? true : !slug.contains("-");
    }

    private Long[] getTagIds(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        List<ArticleCategory> categories = categoryService.doNewOrFindByTagString(tags);
        long[] ids = categories.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }


    @UCenterMenu(text = "评论列表", groupId = "comment", order = 0)
    public void comment() {

        Page<ArticleComment> page = commentService._paginateByUserId(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);

        render("article/comment_list.html");
    }

    /**
     * 评论编辑 页面
     */
    public void commentEdit() {
        long id = getIdPara();
        ArticleComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("article/comment_edit.html");
    }

    public void doCommentSave() {
        ArticleComment comment = getBean(ArticleComment.class, "comment");
        commentService.saveOrUpdate(comment);
        renderJson(Ret.ok());
    }

}
