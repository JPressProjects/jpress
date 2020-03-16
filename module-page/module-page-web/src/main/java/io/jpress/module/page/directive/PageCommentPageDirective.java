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
package io.jpress.module.page.directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jboot.web.directive.base.PaginateDirectiveBase;
import io.jpress.commons.directive.DirectveKit;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageComment;
import io.jpress.module.page.service.SinglePageCommentService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.directive
 */
@JFinalDirective("pageCommentPage")
public class PageCommentPageDirective extends JbootDirectiveBase {

    @Inject
    private SinglePageCommentService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        int page = controller.getParaToInt(1, 1);
        int pageSize = getParaToInt("pageSize", scope, 10);

        SinglePage singlePage = controller.getAttr("page");
        if (singlePage != null) {
            Page<SinglePageComment> articlePage = service.paginateByPageIdInNormal(page, pageSize, singlePage.getId());
            scope.setGlobal("commentPage", articlePage);
            renderBody(env, scope, writer);
        }
    }


    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("pageCommentPaginate")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        @Override
        protected String getUrl(int pageNumber, Env env, Scope scope, Writer writer) {
            HttpServletRequest request = JbootControllerContext.get().getRequest();
            String url = DirectveKit.replacePageNumber(request.getRequestURI(), pageNumber);
            String anchor = getPara("anchor", scope);
            return StrUtil.isBlank(anchor) ? url : url + "#" + anchor;
        }

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("commentPage");
        }

    }
}
