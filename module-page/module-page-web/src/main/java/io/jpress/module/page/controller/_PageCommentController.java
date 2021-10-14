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
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.User;
import io.jpress.module.page.model.SinglePageComment;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping(value = "/admin/page/comment", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _PageCommentController extends AdminControllerBase {


    @Inject
    private SinglePageCommentService commentService;


    @AdminMenu(text = "评论", groupId = "page", order = 5)
    public void list() {

        String status = getPara("status");
        String key = getPara("keyword");
        Long articleId = getParaToLong("pageId");

        Page<SinglePageComment> page =
                StrUtil.isBlank(status)
                        ? commentService._paginateWithoutTrash(getPagePara(), getPageSizePara(), articleId, key)
                        : commentService._paginateByStatus(getPagePara(), getPageSizePara(), articleId, key, status);

        setAttr("page", page);

        long unauditedCount = commentService.findCountByStatus(SinglePageComment.STATUS_UNAUDITED);
        long trashCount = commentService.findCountByStatus(SinglePageComment.STATUS_TRASH);
        long normalCount = commentService.findCountByStatus(SinglePageComment.STATUS_NORMAL);

        setAttr("unauditedCount", unauditedCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", unauditedCount + trashCount + normalCount);

        render("page/comment_list.html");
    }


    /**
     * 删除评论
     */
    public void doDel() {
        Long id = getParaToLong("id");
        commentService.deleteById(id);
        renderOkJson();
    }


    /**
     * 批量删除评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.batchDeleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 批量审核评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doAuditByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.batchChangeStatusByIds(SinglePageComment.STATUS_NORMAL, idsSet.toArray()) ? OK : FAIL);
    }

    /**
     * 修改评论状态
     *
     * @param id
     * @param status
     */
    public void doChangeStatus(Long id, String status) {
        render(commentService.doChangeStatus(id, status) ? OK : FAIL);
    }


    /**
     * 评论回复 页面
     */
    public void reply() {
        long id = getIdPara();
        SinglePageComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("page/comment_reply.html");
    }

    /**
     * 进行评论回复
     */
    public void doReply(Long pageId, Long pid) {
        User user = getLoginedUser();

        SinglePageComment comment = new SinglePageComment();
        comment.setContent(getCleanedOriginalPara("content"));
        comment.setUserId(user.getId());
        comment.setStatus(SinglePageComment.STATUS_NORMAL);
        comment.setPageId(pageId);
        comment.setPid(pid);

        commentService.save(comment);
        renderOkJson();
    }

    /**
     * 评论编辑 页面
     */
    public void edit() {
        long id = getIdPara();
        SinglePageComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("page/comment_edit.html");
    }


    public void doSave() {
        SinglePageComment comment = getBean(SinglePageComment.class, "comment");
        comment.setContent(getCleanedOriginalPara("comment.content"));
        commentService.saveOrUpdate(comment);
        renderOkJson();
    }


}
