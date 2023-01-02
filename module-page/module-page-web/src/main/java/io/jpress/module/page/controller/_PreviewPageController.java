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
package io.jpress.module.page.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.interceptor.AdminInterceptor;
import io.jpress.web.interceptor.TemplateInterceptor;
import io.jpress.web.interceptor.UserInterceptor;

@RequestMapping("/admin/page/preview")
@Before({
        TemplateInterceptor.class,
        AdminInterceptor.class,
        UserInterceptor.class,})
public class _PreviewPageController extends TemplateControllerBase {

    @Inject
    private SinglePageService pageService;

    @Inject
    private SinglePageCategoryService categoryService;


    public void index() {
        SinglePage page =getSinglePage();

        render404If(page == null );

        page.setTitle("（预览中...）" + page.getTitle());

        //设置页面的seo信息
        setSeoInfos(page);

        //设置菜单高亮
        doFlagMenuActive(page);

        //记录当前浏览量
        pageService.doIncViewCount(page.getId());

        setAttr("page", page);

        render(page.getHtmlView());
    }

    private void setSeoInfos(SinglePage page) {
        setSeoTitle(StrUtil.isBlank(page.getMetaTitle()) ? page.getTitle() : page.getMetaTitle());
        setSeoKeywords(page.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(page.getMetaDescription())
                ? CommonsUtils.maxLength(page.getText(), 100)
                : page.getMetaDescription());
    }


    private SinglePage getSinglePage(){
        String idOrSlug = getIdOrSlug();
        if (StrUtil.isBlank(idOrSlug)){
            return null;
        }
        return StrUtil.isNumeric(idOrSlug)
                ? pageService.findById(idOrSlug)
                : pageService.findFirstBySlug(StrUtil.urlDecode(idOrSlug));
    }


    private void doFlagMenuActive(SinglePage page) {

        setMenuActive(menu -> menu.isUrlStartWidth(page.getUrl()));

        SinglePageCategory pageCategory = categoryService.findById(page.getCategoryId());
        if(pageCategory == null){
            return;
        }

        setMenuActive(menu -> {
            if ("single_page_category".equals(menu.getRelativeTable())) {
                if (pageCategory.getId().equals(menu.getRelativeId())) {
                    return true;
                }
            }
            return false;
        });

    }

}
