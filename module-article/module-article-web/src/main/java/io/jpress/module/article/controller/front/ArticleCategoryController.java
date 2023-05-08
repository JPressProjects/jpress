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
package io.jpress.module.article.controller.front;

import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.web.base.TemplateControllerBase;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title:  文章分类的 Controller，主要是用过 {{@link io.jpress.module.article.directive.ArticlePageDirective 来渲染的}}
 * @Package io.jpress.module.article
 */
@RequestMapping("/category")
public class ArticleCategoryController extends TemplateControllerBase {

    @Inject
    private ArticleCategoryService categoryService;


    public void index() {

        if (StrUtil.isBlank(getPara())) {
            redirect("/category/index");
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

        //文章首页高亮
        if (currentCategory == null) {
            setMenuActive(menu -> menu.isUrlEquals("/category")
                    || menu.isUrlEquals("/category/")
                    || menu.isUrlEquals("/category/index"));
        } else {
            setMenuActive(menu -> {
                if (menu.isUrlEquals(CommonsUtils.removeSuffix(currentCategory.getUrl()))) {
                    return true;
                }

                if ("article_category".equals(menu.getRelativeTable())
                        && currentCategory.getId().equals(menu.getRelativeId())) {
                    return true;
                }
                return false;
            });
        }

    }

    private void setSeoInfos(ArticleCategory category) {
        if (category == null) {
            return;
        }

        setSeoTitle(StrUtil.isBlank(category.getMetaTitle()) ? category.getTitle() : category.getMetaTitle());
        setSeoKeywords(category.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(category.getMetaDescription())
                ? CommonsUtils.maxLength(category.getContent(), 100)
                : category.getMetaDescription());
    }


    private ArticleCategory getCategory() {
        String idOrSlug = getIdOrSlug();

        if (StrUtil.isBlank(idOrSlug)) {
            return null;
        }

        ArticleCategory category = StrUtil.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_CATEGORY, StrUtil.urlDecode(idOrSlug));

        //当 slug 不为空，但是查询出来的category却是null的时候
        //应该404显示
        if (!"index".equals(idOrSlug) && category == null) {
            renderError(404);
        }

        return category;
    }


    private String getRenderView(ArticleCategory category) {
        return category == null  ? "artlist.html" : category.getHtmlView();
    }


}
