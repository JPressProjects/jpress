package io.jpress.module.article.admin;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
import io.jpress.module.article.kits.CategoryKits;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;

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
    private ArticleCategoryService articleCategoryService;

    @AdminMenu(text = "文章管理", groupId = "article", order = 0)
    public void index() {
        Page<Article> page = articleService.paginate(getParaToInt("page", 1), 10);
        setAttr("page", page);
        render("article/list.html");
    }


    @AdminMenu(text = "写文章", groupId = "article", order = 1)
    public void write() {

        int articleId = getParaToInt(0, 0);
        if (articleId > 0) {
            Article article = articleService.findById(articleId);
            if (article == null){
                renderError(404);
                return;
            }
            setAttr("article", article);

        }

        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        CategoryKits.toLayerCategories(categories);
        setAttr("categories", categories);

        List<ArticleCategory> subjects = articleCategoryService.findListByType(ArticleCategory.TYPE_SUBJECT);
        setAttr("subjects", subjects);

        render("article/write.html");
    }


    public void doWriteSave() {
        Article article = getModel(Article.class, "");
        long id = articleService.doGetIdBySaveOrUpdateAction(article);
        renderJson(id > 0 ? Ret.ok().put("id", id) : Ret.fail());
    }


    @EmptyValidate(@Form(name = "article.title"))
    public void save(Article article) {
        boolean success = articleService.saveOrUpdate(article);
        render(success ? Ret.ok() : Ret.fail());
    }


    @AdminMenu(text = "分类", groupId = "article", order = 2)
    public void category() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        CategoryKits.toLayerCategories(categories);
        setAttr("categories", categories);
        render("article/category.html");
    }


    public void categoryedit() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
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
        render("article/category_edit.html");
    }

    public void categorySave() {
        ArticleCategory category = getModel(ArticleCategory.class, "");
        articleCategoryService.saveOrUpdate(category);
        redirect("/admin/article/category");
    }


    @AdminMenu(text = "专题", groupId = "article", order = 3)
    public void subject() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_SUBJECT);
        setAttr("categories", categories);
        render("article/subject.html");
    }

    public void subjectedit() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_SUBJECT);
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
        render("article/subject_edit.html");
    }


    public void subjectSave() {
        ArticleCategory category = getModel(ArticleCategory.class, "");
        articleCategoryService.saveOrUpdate(category);
        redirect("/admin/article/subject");
    }


    @AdminMenu(text = "标签", groupId = "article", order = 4)
    public void tag() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_TAG);
        setAttr("categories", categories);
        render("article/tag.html");
    }


    @AdminMenu(text = "评论", groupId = "article", order = 5)
    public void comment() {
        render("article/comment.html");
    }


    @AdminMenu(text = "订单", groupId = "article", order = 6)
    public void paylist() {
        render("article/paylist.html");
    }

}
