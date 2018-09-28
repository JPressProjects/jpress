package io.jpress.module.article;

import com.jfinal.core.Controller;
import io.jboot.Jboot;
import io.jboot.db.model.Columns;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleListener;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Module 监听器
 * @Description: 每个 module 都应该有这样的一个监听器，用来配置自身Module的信息，比如后台菜单等
 * @Package io.jpress.module.page
 */
public class ArticleModuleLisenter implements ModuleListener {


    @Override
    public String onGetDashboardHtmlBox(Controller controller) {
        List<Article> articles = Jboot.bean(ArticleService.class).findListByColumns(Columns.create(), "id desc", 10);
        controller.setAttr("articles", articles);

        ArticleCommentService commentService = Jboot.bean(ArticleCommentService.class);
        List<ArticleComment> articleComments = commentService.findListByColumns(Columns.create().ne("status", ArticleComment.STATUS_TRASH), "id desc", 10);
        controller.setAttr("articleComments", articleComments);

        return "article/_dashboard_box.html";
    }

    @Override
    public void onConfigAdminMenu(List<MenuGroup> adminMenus) {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId("article");
        menuGroup.setText("文章");
        menuGroup.setIcon("<i class=\"fa fa-fw fa-file-text\"></i>");
        menuGroup.setOrder(1);

        adminMenus.add(menuGroup);

    }

    @Override
    public void onConfigUcenterMenu(List<MenuGroup> adminMenus) {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId("article");
        menuGroup.setText("文章");
        menuGroup.setIcon("<i class=\"fa fa-fw fa-file-text\"></i>");
        menuGroup.setOrder(1);

        adminMenus.add(menuGroup);

    }
}
