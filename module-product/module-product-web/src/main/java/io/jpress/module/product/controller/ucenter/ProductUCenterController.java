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
package io.jpress.module.product.controller.ucenter;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.base.UcenterControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章前台页面Controller
 * @Package io.jpress.module.product.admin
 */
@RequestMapping(value = "/ucenter/product",viewPath = "/WEB-INF/views/ucenter/")
public class ProductUCenterController extends UcenterControllerBase {

    @Inject
    private ProductService articleService;

    @Inject
    private ProductCategoryService categoryService;

    @Inject
    private ProductCommentService commentService;



    @UCenterMenu(text = "产品评论", groupId = "comment", order = 0, icon = "<i class=\"fas fa-comment\"></i>")
    public void comment() {
        Page<ProductComment> page = commentService._paginateByUserId(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);
        render("product/comment_list.html");
    }


    public void doCommentDel() {

        ProductComment comment = commentService.findById(getLong("id"));
        if (comment == null) {
            renderFailJson();
            return;
        }

        if (notLoginedUserModel(comment)){
            renderFailJson("非法操作");
            return;
        }

        renderJson(commentService.delete(comment) ? OK : FAIL);
    }

}
