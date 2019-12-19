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
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.User;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;
import java.util.Set;


@RequestMapping(value = "/admin/product/comment", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ProductCommentController extends AdminControllerBase {

    @Inject
    private ProductCommentService commentService;

    @AdminMenu(text = "评论", groupId = "product", order = 88)
    public void index() {
        Integer status = getParaToInt("status");
        String key = getPara("keyword");
        Long productId = getParaToLong("productId");

        Page<ProductComment> page =
                status == null
                        ? commentService._paginateWithoutTrash(getPagePara(), 10, productId, key)
                        : commentService._paginateByStatus(getPagePara(), 10, productId, key, status);

        setAttr("page", page);


        long unauditedCount = commentService.findCountByStatus(ProductComment.STATUS_UNAUDITED);
        long trashCount = commentService.findCountByStatus(ProductComment.STATUS_TRASH);
        long normalCount = commentService.findCountByStatus(ProductComment.STATUS_NORMAL);

        setAttr("unauditedCount", unauditedCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", unauditedCount + trashCount + normalCount);

        render("product/comment_list.html");
    }


    public void edit() {
        int entryId = getParaToInt(0, 0);

        ProductComment entry = entryId > 0 ? commentService.findById(entryId) : null;
        setAttr("productComment", entry);
        set("now", new Date());
        render("product/comment_edit.html");
    }


    public void reply() {
        long id = getIdPara();
        ProductComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("product/comment_reply.html");
    }


    public void doReply(String content, Long productId, Long pid) {
        User user = getLoginedUser();

        ProductComment comment = new ProductComment();
        comment.setContent(content);
        comment.setUserId(user.getId());
        comment.setAuthor(user.getNickname());
        comment.setStatus(ProductComment.STATUS_NORMAL);
        comment.setProductId(productId);
        comment.setPid(pid);

        commentService.save(comment);
        renderOkJson();
    }




    public void doSave() {
        ProductComment entry = getModel(ProductComment.class, "productComment");
        commentService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }


    public void doDel() {
        Long id = getIdPara();
        render(commentService.deleteById(id) ? Ret.ok() : Ret.fail());
    }


    public void doCommentStatusChange(Long id, int status) {
        render(commentService.doChangeStatus(id, status) ? OK : FAIL);
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds(){
        Set<String> idsSet = getParaSet("ids");
        render(commentService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }
}