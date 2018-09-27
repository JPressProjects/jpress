package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.ApiControllerBase;

import javax.inject.Inject;

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
        if (id != null) {
            Article article = articleService.findById(id);
            renderJson(Ret.ok("article", article));
            return;
        }

        String slug = getPara("slug");
        if (slug != null) {
            Article article = articleService.findFirstBySlug(slug);
            renderJson(Ret.ok("article", article));
            return;
        }

        renderFailJson();
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

        if (StrUtils.isBlank(slug)
                || StrUtils.isBlank(type)) {
            renderFailJson();
            return;
        }


        ArticleCategory category = categoryService.findFirstByTypeAndSlug(type, slug);
        renderJson(Ret.ok("category", category));
    }


    /**
     * 文章类别的API
     */
    public void list() {

    }


}
