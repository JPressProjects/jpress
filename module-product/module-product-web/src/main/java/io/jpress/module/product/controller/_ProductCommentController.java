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
package io.jpress.module.product.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;


@RequestMapping(value = "/admin/product/comment", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ProductCommentController extends AdminControllerBase {

    @Inject
    private ProductCommentService productCommentService;

    @AdminMenu(text = "评论", groupId = "product", order = 99)
    public void index() {
        Page<ProductComment> entries = productCommentService.paginate(getPagePara(), 10);
        setAttr("page", entries);
        render("product/comment_list.html");
    }


    public void edit() {
        int entryId = getParaToInt(0, 0);

        ProductComment entry = entryId > 0 ? productCommentService.findById(entryId) : null;
        setAttr("productComment", entry);
        set("now", new Date());
        render("product/comment_edit.html");
    }

    public void doSave() {
        ProductComment entry = getModel(ProductComment.class, "productComment");
        productCommentService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }


    public void doDel() {
        Long id = getIdPara();
        render(productCommentService.deleteById(id) ? Ret.ok() : Ret.fail());
    }
}