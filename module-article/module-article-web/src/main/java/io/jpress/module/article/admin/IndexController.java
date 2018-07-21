package io.jpress.module.article.admin;

import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.web.base.AdminControllerBase;
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
public class IndexController extends AdminControllerBase {

    @Inject
    private ArticleService articleService;
    @Inject
    private ArticleCategoryService articleCategoryService;

    @AdminMenu(text = "文章管理", groupId = "article", order = 0)
    public void index() {
        render("article/list.html");
    }


    @AdminMenu(text = "写文章", groupId = "article", order = 1)
    public void write() {
        render("article/write.html");
    }


    @EmptyValidate(@Form(name = "article.title"))
    public void save(Article article) {
        boolean success = articleService.saveOrUpdate(article);
        render(success ? Ret.ok() : Ret.fail());
    }


    @AdminMenu(text = "分类", groupId = "article", order = 2)
    public void category() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        setAttr("categories", categories);
        render("article/category.html");
    }

    @AdminMenu(text = "专题", groupId = "article", order = 3)
    public void subject() {
        List<ArticleCategory> categories = articleCategoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        setAttr("categories", categories);
        render("article/category.html");
    }

    public void categoryedit() {
        int id = getParaToInt(0, 0);
        if (id > 0) {
            setAttr("category", articleCategoryService.findById(id));
        }
        render("article/category_edit.html");
    }


    @AdminMenu(text = "标签", groupId = "article", order = 4)
    public void tag() {
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
