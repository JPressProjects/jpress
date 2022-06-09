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
package io.jpress.module.page.controller;

import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.web.base.TemplateControllerBase;


/**
 *
 * @version V5.x
 * @Title:  页面分类的 Controller，主要是用过 {{@link io.jpress.module.page.directive.PageListDirective 来渲染的}}
 * @Package io.jpress.module.article
 */
@RequestMapping("/page/category")
public class PageCategoryController extends TemplateControllerBase {

    @Inject
    private SinglePageCategoryService categoryService;


    public void index() {

        if (StrUtil.isBlank(getPara())) {
            redirect("/page/category/index");
            return;
        }

        SinglePageCategory category = getCategory();
        setAttr("category", category);
        setSeoInfos(category);

        //标识菜单高亮
        doFlagMenuActive(category);


        render(getRenderView(category));
    }

    private void doFlagMenuActive(SinglePageCategory currentCategory) {

        //页面首页高亮
        if (currentCategory == null) {
            setMenuActive(menu -> menu.isUrlEquals("/page/category")
                    || menu.isUrlEquals("/page/category/")
                    || menu.isUrlEquals("/page/category/index"));
        } else {
            setMenuActive(menu -> {
                if (menu.isUrlEquals(CommonsUtils.removeSuffix(currentCategory.getUrl()))) {
                    return true;
                }

                if ("single_page_category".equals(menu.getRelativeTable())
                        && currentCategory.getId().equals(menu.getRelativeId())) {
                    return true;
                }
                return false;
            });
        }

    }

    private void setSeoInfos(SinglePageCategory category) {
        if (category == null) {
            return;
        }

        setSeoTitle(category.getTitle());
        setSeoKeywords(category.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(category.getMetaDescription())
                ? CommonsUtils.maxLength(category.getContent(), 100)
                : category.getMetaDescription());
    }


    private SinglePageCategory getCategory() {
        String idOrSlug = getIdOrSlug();

        if (StrUtil.isBlank(idOrSlug)) {
            return null;
        }

        SinglePageCategory category = StrUtil.isNumeric(idOrSlug)
                ? categoryService.findById(idOrSlug)
                : categoryService.findFirstByTypeAndSlug(SinglePageCategory.TYPE_CATEGORY, StrUtil.urlDecode(idOrSlug));

        //当 slug 不为空，但是查询出来的category却是null的时候
        //应该404显示
        if (!"index".equals(idOrSlug) && category == null) {
            renderError(404);
        }

        return category;
    }


    private String getRenderView(SinglePageCategory category) {
        return category == null  ? "pagelist.html" : category.getHtmlView();
    }


}
