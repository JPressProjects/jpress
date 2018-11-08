/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.article.controller;

import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.web.base.TemplateControllerBase;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/article/category")
public class ArticleCategoryController extends TemplateControllerBase {

    @Inject
    private ArticleCategoryService categoryService;


    public void index() {

        if (StrUtils.isBlank(getPara())) {
            redirect("/article/category/index");
            return;
        }

        ArticleCategory category = getCategory();
        setAttr("category", category);
        setSeoInfos(category);

        //标识菜单高亮
        doFlagMenuActive(category);


        render(getRenderView(category));
    }

    private void doFlagMenuActive(ArticleCategory currentCategory) {

        if (currentCategory == null) {
            return;
        }

        setMenuActive(menu -> {
            if (CommonsUtils.removeSuffix(menu.getUrl())
                    .equals(CommonsUtils.removeSuffix(currentCategory.getUrl()))) {
                return true;
            }

            if ("article_category".equals(menu.getRelativeTable())
                    && currentCategory.getId().equals(menu.getRelativeId())) {
                return true;
            }
            return false;
        });

    }

    private void setSeoInfos(ArticleCategory category) {
        if (category == null) {
            return;
        }

        setSeoTitle(category.getTitle());
        setSeoKeywords(category.getMetaKeywords());
        setSeoDescription(StrUtils.isBlank(category.getMetaDescription())
                ? CommonsUtils.maxLength(category.getContent(), 100)
                : category.getMetaDescription());
    }


    private ArticleCategory getCategory() {
        String idOrSlug = getPara(0);

        if (StrUtils.isBlank(idOrSlug)) {
            return null;
        }

        ArticleCategory category = StrUtils.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_CATEGORY, StrUtils.urlDecode(idOrSlug));

        //当 slug 不为空，但是查询出来的category却是null的时候
        //应该404显示
        if (!"index".equals(idOrSlug) && category == null) {
            renderError(404);
        }

        return category;
    }

    private String getRenderView(ArticleCategory category) {
        return category == null
                ? "artlist.html"
                : category.getHtmlView();
    }


}
