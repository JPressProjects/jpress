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

import com.google.common.collect.Sets;
import com.jfinal.aop.Inject;
import com.jfinal.core.NotAction;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.handler.JPressHandler;

import java.util.Set;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/page")
public class PageController extends TemplateControllerBase {

    @Inject
    private SinglePageService pageService;

    @Inject
    private SinglePageCategoryService pageCategoryService;

    @Inject
    private OptionService optionService;

    private static final Set<String> excludePage = Sets.newHashSet("setting", "setting_v4", "layout", "header", "footer");

    public void index() {

        String idOrSlug = getIdOrSlug();

        SinglePage singlePage = StrUtil.isNumeric(idOrSlug)
                ? pageService.findById(idOrSlug)
                : pageService.findFirstBySlug(idOrSlug);

        if (singlePage == null || !singlePage.isNormal()) {
            renderTemplateView(idOrSlug);
        } else {
            renderPage(singlePage, idOrSlug);
        }

    }

    @Override
    @NotAction
    public String getIdOrSlug() {
        String idOrSlug = StrUtil.urlDecode(JPressHandler.getCurrentTarget()).substring(1);
        if (StrUtil.isBlank(idOrSlug)) {
            return idOrSlug;
        }

        int indexOf = idOrSlug.lastIndexOf("-");
        if (indexOf == -1) {
            return idOrSlug;
        }

        String lastString = idOrSlug.substring(indexOf + 1);
        if (StrUtil.isNumeric(lastString)) {
            return idOrSlug.substring(0, indexOf);
        } else {
            return idOrSlug;
        }
    }


    private void renderPage(SinglePage page, String slugOrId) {
        pageService.doIncViewCount(page.getId());

        //设置SEO信息
        setSeoInfos(page);

        //设置菜单高亮
        setMenuActive(menu -> menu.getUrl().indexOf("/") <= 1 && menu.isUrlStartWidth("/" + slugOrId));

        setAttr("page", page);

        String pageStyle = page.getStyle();
        if (StrUtil.isNotBlank(pageStyle)) {
            render(page.getHtmlView());
            return;
        }


        SinglePageCategory pageCategory = pageCategoryService.findById(page.getCategoryId());
        if (pageCategory != null) {
            render(pageCategory.getPageView());
        } else {
            render(page.getHtmlView());
        }
    }


    private void renderTemplateView(String slugOrId) {
        if (excludePage.contains(slugOrId)) {
            renderError(404);
            return;
        }

        String htmlView = slugOrId + ".html";
        if (hasTemplate(htmlView)) {
            //设置菜单高亮
            setMenuActive(menu -> menu.isUrlStartWidth("/" + slugOrId));
            render(htmlView);
        } else {
            renderError(404);
        }
    }


    private void setSeoInfos(SinglePage page) {
        setSeoTitle(StrUtil.isBlank(page.getMetaTitle()) ? page.getTitle() : page.getMetaTitle());
        setSeoKeywords(page.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(page.getMetaDescription())
                ? CommonsUtils.maxLength(page.getText(), 100)
                : page.getMetaDescription());
    }



}
