/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.article.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.model.User;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.interceptor.AdminInterceptor;
import io.jpress.web.interceptor.TemplateInterceptor;
import io.jpress.web.interceptor.UserInterceptor;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/admin/article/preview")
@Before({
        TemplateInterceptor.class,
        AdminInterceptor.class,
        UserInterceptor.class,})
public class _PreviewArticleController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private UserService userService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;


    public void index() {
        Article article = getArticle();

        //当文章处于审核中、草稿、或者未到发布时间时候，显示404
        render404If(article == null );

        article.setTitle("（预览中...）" + article.getTitle());

        //设置页面的seo信息
        setSeoInfos(article);


        //设置菜单高亮
        doFlagMenuActive(article);

        //记录当前浏览量
        articleService.doIncArticleViewCount(article.getId());

        User articleAuthor = article.getUserId() != null
                ? userService.findById(article.getUserId())
                : null;

        article.put("user", articleAuthor);

        setAttr("article", article);

        render(article.getHtmlView());
    }

    private void setSeoInfos(Article article) {
        setSeoTitle(StrUtil.isBlank(article.getMetaTitle()) ? article.getTitle() : article.getMetaTitle());
        setSeoKeywords(article.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(article.getMetaDescription())
                ? CommonsUtils.maxLength(article.getText(), 100)
                : article.getMetaDescription());
    }


    private Article getArticle() {
        String idOrSlug = getIdOrSlug();
        if (StrUtil.isBlank(idOrSlug)){
            return null;
        }
        return StrUtil.isNumeric(idOrSlug)
                ? articleService.findById(idOrSlug)
                : articleService.findFirstBySlug(StrUtil.urlDecode(idOrSlug));
    }


    private void doFlagMenuActive(Article article) {

        setMenuActive(menu -> menu.isUrlStartWidth(article.getUrl()));

        List<ArticleCategory> articleCategories = categoryService.findListByArticleId(article.getId());
        if (articleCategories == null || articleCategories.isEmpty()) {
            return;
        }

        setMenuActive(menu -> {
            if ("article_category".equals(menu.getRelativeTable())) {
                for (ArticleCategory category : articleCategories) {
                    if (category.getId().equals(menu.getRelativeId())) {
                        return true;
                    }
                }
            }
            return false;
        });

    }




}
