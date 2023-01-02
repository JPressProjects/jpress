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
package io.jpress.module.page.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.JPressActiveKit;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.service.SinglePageService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("pages")
public class PagesDirective extends JbootDirectiveBase {

    @Inject
    private SinglePageService pageService;

    @Inject
    private SinglePageCategoryService categoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("flag", scope);
        String categroyFlag = getPara("categoryFlag", scope);
        boolean withSameCategory = getParaToBool("withSameCategory", scope, false);
        boolean withSameFlag = getParaToBool("withSameFlag", scope, false);


        List<SinglePage> singlePages = null;
        if (StrUtil.isNotBlank(flag)) {
            singlePages = pageService.findListByFlag(flag);
        }

        //根据分类的 flag 进行查询
        else if (StrUtil.isNotBlank(categroyFlag)) {
            SinglePageCategory category = categoryService.findFirstByColumns(Columns.create("flag", categroyFlag));
            if (category != null) {
                singlePages = pageService.findListByCategoryId(category.getId());
            }
        }

        //获取相同分类的页面
        else if (withSameCategory) {
            SinglePage currentPage = JbootControllerContext.get().getAttr("page");
            if (currentPage != null && currentPage.getCategoryId() != null) {
                singlePages = pageService.findListByCategoryId(currentPage.getCategoryId());
            }
        }

        //获取相同 tag 的页面
        else if (withSameFlag) {
            SinglePage currentPage = JbootControllerContext.get().getAttr("page");
            if (currentPage != null && StrUtil.isNotBlank(currentPage.getFlag())) {
                singlePages = pageService.findListByFlag(currentPage.getFlag());
            }
        }

        if (singlePages == null || singlePages.isEmpty()) {
            return;
        }

        singlePages.stream().filter(singlePage -> singlePage.isNormal());

        //设置页面高亮
        doFlagIsActiveByCurrentPage(singlePages);

        scope.setLocal("pages", singlePages);

        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }

    private void doFlagIsActiveByCurrentPage(List<SinglePage> pages) {
        SinglePage currentPage = JbootControllerContext.get().getAttr("page");

        //当前url并不是页面详情
        if (currentPage == null) {
            return;
        }

        for (SinglePage page : pages) {
            if (page.equals(currentPage)) {
                JPressActiveKit.makeItActive(page);
            }
        }
    }
}
