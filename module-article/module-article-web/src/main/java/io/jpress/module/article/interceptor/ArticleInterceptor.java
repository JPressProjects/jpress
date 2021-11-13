package io.jpress.module.article.interceptor;

import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import io.jboot.utils.RequestUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.model.User;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.interceptor.UserInterceptor;

public class ArticleInterceptor implements Interceptor {
    @Inject
    private ArticleService articleService;
    private static final String ATTR_ARTICLE = "article";

    public static Article getThreadLocalArticle() {
        return JbootControllerContext.get().getAttr(ATTR_ARTICLE);
    }

    @Override
    public void intercept(Invocation inv) {

        Controller c = inv.getController();

        Long productId = inv.getController().getLong("id");
        Article article = articleService.findById(productId);

        if (article == null || !article.isNormal()) {
            if (RequestUtil.isAjaxRequest(c.getRequest())) {
                c.renderJson(Ret.fail().set("code", "2").set("message", "文章不存在或已删除。"));
            } else {
                c.renderError(404);
            }
            return;
        }


        User user = UserInterceptor.getThreadLocalUser();
        if (user == null) {
            if (RequestUtil.isAjaxRequest(c.getRequest())) {
                c.renderJson(Ret.fail()
                        .set("code", 1)
                        .set("message", "用户未登录")
                        .set("gotoUrl", JFinal.me().getContextPath() + "/user/login?gotoUrl=" + article.getUrl()));
            } else {
                c.redirect("/user/login?gotoUrl=" + article.getUrl());
            }
            return;
        }

        c.setAttr(ATTR_ARTICLE, article);
        inv.invoke();
    }
}