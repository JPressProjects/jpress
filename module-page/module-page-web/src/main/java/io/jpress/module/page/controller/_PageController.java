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
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.User;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageComment;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping(value = "/admin/page", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _PageController extends AdminControllerBase {

    @Inject
    private SinglePageService sps;

    @Inject
    private SinglePageCommentService commentService;

    @AdminMenu(text = "页面管理", groupId = "page", order = 1)
    public void index() {

        String status = getPara("status");
        String title = getPara("title");

        Page<SinglePage> page =
                StrUtil.isBlank(status)
                        ? sps._paginateWithoutTrash(getPagePara(), 10, title)
                        : sps._paginateByStatus(getPagePara(), 10, title, status);

        setAttr("page", page);

        int draftCount = sps.findCountByStatus(SinglePage.STATUS_DRAFT);
        int trashCount = sps.findCountByStatus(SinglePage.STATUS_TRASH);
        int normalCount = sps.findCountByStatus(SinglePage.STATUS_NORMAL);

        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);

        render("page/page_list.html");
    }

    @AdminMenu(text = "新建", groupId = "page", order = 2)
    public void write() {
        int pageId = getParaToInt(0, 0);

        SinglePage page = pageId > 0 ? sps.findById(pageId) : null;
        setAttr("page", page);

        Template template = TemplateManager.me().getCurrentTemplate();
        if (template != null) {
            List<String> styles = template.getSupportStyles("page_");
            setAttr("styles", styles);
            setAttr("flags", template.getFlags());
        }

        String editMode = page == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : page.getEditMode();
        setAttr("editMode", StrUtil.isBlank(editMode) ? "html" : editMode);

        render("page/page_write.html");
    }


    @AdminMenu(text = "评论", groupId = "page", order = 5)
    public void comment() {

        String status = getPara("status");
        String key = getPara("keyword");
        Long articleId = getParaToLong("pageId");

        Page<SinglePageComment> page =
                StrUtil.isBlank(status)
                        ? commentService._paginateWithoutTrash(getPagePara(), 10, articleId, key)
                        : commentService._paginateByStatus(getPagePara(), 10, articleId, key, status);

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
    public void doCommentDel() {
        Long id = getParaToLong("id");
        commentService.deleteById(id);
        renderOkJson();
    }


    /**
     * 批量删除评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doCommentDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.batchDeleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 批量审核评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doCommentAuditByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.batchChangeStatusByIds(SinglePageComment.STATUS_NORMAL, idsSet.toArray()) ? OK : FAIL);
    }

    /**
     * 修改评论状态
     *
     * @param id
     * @param status
     */
    public void doCommentStatusChange(Long id, String status) {
        render(commentService.doChangeStatus(id, status) ? OK : FAIL);
    }


    /**
     * 评论回复 页面
     */
    public void commentReply() {
        long id = getIdPara();
        SinglePageComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("page/comment_reply.html");
    }

    /**
     * 进行评论回复
     */
    public void doCommentReply(String content, Long pageId, Long pid) {
        User user = getLoginedUser();

        SinglePageComment comment = new SinglePageComment();
        comment.setContent(content);
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
    public void commentEdit() {
        long id = getIdPara();
        SinglePageComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("page/comment_edit.html");
    }


    @AdminMenu(text = "设置", groupId = "page", order = 6)
    public void setting() {
        render("page/setting.html");
    }


    @EmptyValidate({
            @Form(name = "id", message = "页面ID不能为空"),
            @Form(name = "mode", message = "页面编辑模式不能为空")
    })
    public void doChangeEditMode() {
        Long id = getParaToLong("id");
        String mode = getPara("mode");

        SinglePage page = sps.findById(id);
        if (page == null) {
            renderFailJson();
            return;
        }

        page.setEditMode(mode);
        sps.update(page);
        renderOkJson();
    }


    @EmptyValidate({
            @Form(name = "page.title", message = "标题不能为空"),
            @Form(name = "page.content", message = "内容不能为空")
    })
    public void doWriteSave() {
        SinglePage page = getModel(SinglePage.class, "page");

        if (!validateSlug(page)) {
            renderJson(Ret.fail("message", "slug不能包含该字符：- "));
            return;
        }

        if (StrUtil.isNotBlank(page.getSlug())) {
            SinglePage bySlug = sps.findFirstBySlug(page.getSlug());
            if (bySlug != null && bySlug.getId().equals(page.getId()) == false) {
                renderJson(Ret.fail("message", "该slug已经存在"));
                return;
            }
        }

        sps.saveOrUpdate(page);
        renderJson(Ret.ok().set("id", page.getId()));
    }


    public void doDel() {
        Long id = getIdPara();
        render(sps.deleteById(id) ? OK : FAIL);
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(sps.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    public void doTrash() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_TRASH) ? OK : FAIL);
    }

    public void doDraft() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_DRAFT) ? OK : FAIL);
    }

    public void doNormal() {
        Long id = getIdPara();
        render(sps.doChangeStatus(id, SinglePage.STATUS_NORMAL) ? OK : FAIL);
    }
}
