package io.jpress.module.article.controller;

import io.jboot.utils.StringUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.TemplateControllerBase;

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

    public void index() {
        Article article = getArticle();
        if (article == null) {
            renderError(404);
            return;
        }

        Long page = getParaToLong(1);

        setAttr("article", article);
        render(article.getHtmlView());
    }

    private Article getArticle() {
        String idOrSlug = getPara(0);

        return StringUtils.isNumeric(idOrSlug)
                ? articleService.findById(idOrSlug)
                : articleService.findFirstBySlug(idOrSlug);
    }


    public void category() {

    }

    public void subject() {

    }

    public void tag() {

    }

}
