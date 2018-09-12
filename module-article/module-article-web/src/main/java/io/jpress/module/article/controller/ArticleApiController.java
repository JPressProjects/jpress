package io.jpress.module.article.controller;

import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/api/article")
public class ArticleApiController extends TemplateControllerBase {


    public void index() {


        render("article.html");
    }

    public void category() {

    }

    public void subject() {

    }

    public void tag() {
        
    }

}
