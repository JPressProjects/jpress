package io.jpress.module.article.admin;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.article.kits.CategoryKits;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/admin/article")
public class _ArticleController extends AdminControllerBase {

    @Inject
    private ArticleService articleService;
    @Inject
    private ArticleCategoryService categoryService;
    @Inject
    private ArticleCommentService commentService;

    @AdminMenu(text = "文章管理", groupId = "article", order = 0)
    public void index() {
        String status = getPara("s");

        Page<Article> page =
                status == null
                        ? articleService.paginateWithoutTrash(getPagePara(), 10)
                        : articleService.paginateByStatus(getPagePara(), 10, status);

        setAttr("page", page);

        int draftCount = articleService.findCountByStatus(Article.STATUS_DRAFT);
        int trashCount = articleService.findCountByStatus(Article.STATUS_TRASH);
        int normalCount = articleService.findCountByStatus(Article.STATUS_NORMAL);

        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);

        render("article/list.html");
    }


    @AdminMenu(text = "写文章", groupId = "article", order = 1)
    public void write() {

        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        CategoryKits.toLayerCategories(categories);
        setAttr("categories", categories);

        List<ArticleCategory> subjects = categoryService.findListByType(ArticleCategory.TYPE_SUBJECT);
        setAttr("subjects", subjects);

        int articleId = getParaToInt(0, 0);

        if (articleId > 0) {
            Article article = articleService.findById(articleId);
            if (article == null) {
                renderError(404);
                return;
            }
            setAttr("article", article);

            List<ArticleCategory> tags = categoryService.findTagListByArticleId(articleId);
            setAttr("tags", tags);

            Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
            flagCheck(categories, categoryIds);
            flagCheck(subjects, categoryIds);
        }

        render("article/write.html");
    }

    private void flagCheck(List<ArticleCategory> categories, Long[] checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (ArticleCategory category : categories) {
            for (Long id : checkIds) {
                if (id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
    }


    @EmptyValidate({
            @Form(name = "article.title", message = "标题不能为空"),
            @Form(name = "article.text", message = "内容不能为空")
    })
    public void doWriteSave() {
        Article article = getModel(Article.class, "article");

        if (article.getCommentStatus() == null) {
            article.setCommentStatus(false);
        }

        long id = articleService.doGetIdBySaveOrUpdateAction(article);

        Long[] categoryIds = getParaValuesToLong("category");
        Long[] subjectIds = getParaValuesToLong("subject");
        Long[] tagIds = getTagIds(getParaValues("tag"));

        Long[] allIds = ArrayUtils.addAll(categoryIds, subjectIds);
        allIds = ArrayUtils.addAll(allIds, tagIds);

        articleService.doUpdateCategorys(id, allIds);

        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret.toJson());
    }

    private Long[] getTagIds(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        List<ArticleCategory> categories = categoryService.doNewOrFindByTagString(tags);
        long[] ids = categories.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }


    @EmptyValidate(@Form(name = "article.title"))
    public void save(Article article) {
        boolean success = articleService.saveOrUpdate(article);
        render(success ? Ret.ok() : Ret.fail());
    }


    @AdminMenu(text = "分类", groupId = "article", order = 2)
    public void category() {
        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        CategoryKits.toLayerCategories(categories);
        setAttr("categories", categories);
        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (ArticleCategory category : categories) {
                if (category.getId() == id) {
                    setAttr("category", category);
                }
            }
        }
        render("article/category.html");
    }


    @AdminMenu(text = "专题", groupId = "article", order = 3)
    public void subject() {
        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_SUBJECT);
        setAttr("categories", categories);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (ArticleCategory category : categories) {
                if (category.getId() == id) {
                    setAttr("category", category);
                }
            }
        }

        render("article/subject.html");
    }


    @AdminMenu(text = "标签", groupId = "article", order = 4)
    public void tag() {
        Page<ArticleCategory> page = categoryService.paginateByType(getPagePara(), 10, ArticleCategory.TYPE_TAG);
        setAttr("page", page);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            setAttr("category", categoryService.findById(id));

        }

        render("article/tag.html");
    }


    public void doCategorySave() {
        ArticleCategory category = getModel(ArticleCategory.class, "category");
        categoryService.saveOrUpdate(category);
        renderJson(Ret.ok());
    }

    public void doCategoryDel() {
        categoryService.deleteById(getIdPara());
        renderJson(Ret.ok());
    }


    @AdminMenu(text = "评论", groupId = "article", order = 5)
    public void comment() {
        Page<ArticleComment> page = commentService.paginate(getPagePara(), 10);
        setAttr("page", page);
        render("article/comment.html");
    }


    @AdminMenu(text = "设置", groupId = "article", order = 6)
    public void setting() {
        render("article/setting.html");
    }


    public void doDel() {
        Long id = getIdPara();
        render(articleService.deleteById(id) ? Ret.ok() : Ret.fail());
    }


    public void doTrash() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_TRASH) ? Ret.ok() : Ret.fail());
    }

    public void doDraft() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_DRAFT) ? Ret.ok() : Ret.fail());
    }

    public void doNormal() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_NORMAL) ? Ret.ok() : Ret.fail());
    }

}
