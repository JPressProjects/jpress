/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.module.article;

import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import com.jfinal.template.Engine;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jboot.db.model.Columns;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.module.ModuleListener;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.sitemap.ArticleSitemapManager;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: Module 监听器
 * @Description: 每个 module 都应该有这样的一个监听器，用来配置自身Module的信息，比如后台菜单等
 * @Package io.jpress.module.page
 */
public class ArticleModuleInitializer extends JbootAppListenerBase implements ModuleListener {


    @Override
    public String onRenderDashboardBox(Controller controller) {
        List<Article> articles = Aop.get(ArticleService.class).findListByColumns(Columns.create().eq("status", Article.STATUS_NORMAL), "id desc", 10);
        controller.setAttr("articles", articles);

        ArticleCommentService commentService = Aop.get(ArticleCommentService.class);
        List<ArticleComment> articleComments = commentService.findListByColumns(Columns.create().ne("status", ArticleComment.STATUS_TRASH), "id desc", 10);
        controller.setAttr("articleComments", articleComments);

        return "article/_dashboard_box.html";
    }

    @Override
    public String onRenderToolsBox(Controller controller) {
        return "article/_tools_box.html";
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
    public void onConfigUcenterMenu(List<MenuGroup> ucenterMenus) {

        MenuGroup articleMenuGroup = new MenuGroup();
        articleMenuGroup.setId("article");
        articleMenuGroup.setText("我的文章");
        articleMenuGroup.setIcon("<i class=\"fa fa-fw fa-file-text\"></i>");
        articleMenuGroup.setOrder(1);
        ucenterMenus.add(articleMenuGroup);

    }

    @Override
    public void onEngineConfig(Engine engine) {
        engine.addSharedFunction("/WEB-INF/views/commons/article/defaultArticleCommentPage.html");
    }

    @Override
    public void onStart() {
        ArticleSitemapManager.me().init();
    }
}
