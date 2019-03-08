package io.jpress.module.article.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.TemplateControllerBase;

/**
 * 文章前台列表页面 Controller
 *
 * @author Eric.Huang
 * @date 2019-03-03 22:38
 * @package io.jpress.module.article.controller
 **/
@RequestMapping("/articles")
public class ArticlesController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    public void paginate() {
        Long categoryId = getParaToLong("categoryId");
        String orderBy = getPara("orderBy");
        int pageNumber = getParaToInt("page", 1);

        Page<Article> page = categoryId == null
                ? articleService.paginateInNormal(pageNumber, 10, orderBy)
                : articleService.paginateByCategoryIdInNormal(pageNumber, 10, categoryId, orderBy);

        renderJson(Ret.ok().set("page", page));

    }
}
