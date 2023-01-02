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
package io.jpress.module.article.controller.admin;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.User;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.admin
 */
@RequestMapping(value = "/admin/article/comment", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ArticleCommentController extends AdminControllerBase {

    @Inject
    private ArticleCommentService commentService;


    @AdminMenu(text = "评论", groupId = "article", order = 5)
    public void list() {
        String status = getPara("status");

        Columns columns = Columns.create()
                .eq("article_id", getParaToLong("articleId"))
                .eq("user_id", getParaToLong("userId"))
                .likeAppendPercent("content", getPara("keyword"));

        Page<ArticleComment> page =
                StrUtil.isBlank(status)
                        ? commentService._paginateWithoutTrash(getPagePara(), getPageSizePara(), columns)
                        : commentService._paginateByStatus(getPagePara(), getPageSizePara(), columns, status);

        setAttr("page", page);

        long unauditedCount = commentService.findCountByStatus(ArticleComment.STATUS_UNAUDITED);
        long trashCount = commentService.findCountByStatus(ArticleComment.STATUS_TRASH);
        long normalCount = commentService.findCountByStatus(ArticleComment.STATUS_NORMAL);

        setAttr("unauditedCount", unauditedCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", unauditedCount + trashCount + normalCount);

        render("article/comment_list.html");
    }


    /**
     * 评论回复 页面
     */
    public void reply() {
        long id = getIdPara();
        ArticleComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("article/comment_reply.html");
    }

    /**
     * 评论编辑 页面
     */
    public void edit() {
        long id = getIdPara();
        ArticleComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("article/comment_edit.html");
    }

    public void doSave() {
        ArticleComment comment = getBean(ArticleComment.class, "comment");
        comment.setContent(getCleanedOriginalPara("comment.content"));
        commentService.saveOrUpdate(comment);
        renderOkJson();
    }


    /**
     * 进行评论回复
     */
    public void doReply(Long articleId, Long pid) {
        User user = getLoginedUser();

        ArticleComment comment = new ArticleComment();
        comment.setContent(getCleanedOriginalPara("content"));
        comment.setUserId(user.getId());
        comment.setAuthor(user.getNickname());
        comment.setStatus(ArticleComment.STATUS_NORMAL);
        comment.setArticleId(articleId);
        comment.setPid(pid);

        commentService.save(comment);
        renderOkJson();
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
        render(commentService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 批量审核评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doAuditByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.batchChangeStatusByIds(ArticleComment.STATUS_NORMAL, idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 修改评论状态
     */
    public void doChangeStatus(Long id, String status) {
        render(commentService.doChangeStatus(id, status) ? OK : FAIL);
    }


}
