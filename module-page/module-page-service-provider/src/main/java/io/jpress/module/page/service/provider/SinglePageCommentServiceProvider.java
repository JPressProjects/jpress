package io.jpress.module.page.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ArrayUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.page.model.SinglePageComment;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.service.UserService;

@Bean
public class SinglePageCommentServiceProvider extends JbootServiceBase<SinglePageComment> implements SinglePageCommentService {

    @Inject
    private SinglePageService pageService;

    @Inject
    private UserService userService;


    @Override
    public SinglePageComment findById(Object id) {
        SinglePageComment comment = super.findById(id);
        pageService.join(comment, "page_id", "page");
        userService.join(comment, "user_id");
        return comment;
    }

    @Override
    public long findCountByStatus(String status) {
        return DAO.findCountByColumn(Column.create("status", status));
    }

    @Override
    public Page<SinglePageComment> _paginateByStatus(int page, int pagesize, Long articleId, String keyword, String status) {
        Columns columns = Columns.create("article_id", articleId)
                .add("status", status)
                .likeAppendPercent("content", keyword);

        Page<SinglePageComment> p = DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");

        userService.join(p, "user_id");
        pageService.join(p, "page_id", "page");
        return p;
    }

    @Override
    public Page<SinglePageComment> _paginateWithoutTrash(int page, int pagesize, Long articleId, String keyword) {
        Columns columns = Columns.create("article_id", articleId)
                .ne("status", SinglePageComment.STATUS_TRASH)
                .likeAppendPercent("content", keyword);

        Page<SinglePageComment> p = DAO.paginateByColumns(
                page,
                pagesize,
                columns,
                "id desc");


        userService.join(p, "user_id");
        pageService.join(p, "page_id", "page");
        return p;
    }

    @Override
    public Page<SinglePageComment> paginateByPageIdInNormal(int page, int pagesize, long pageId) {
        Columns columns = Columns.create("page_id", pageId);
        columns.add("status", SinglePageComment.STATUS_NORMAL);


        Page<SinglePageComment> p = DAO.paginateByColumns(
                page,
                pagesize,
                columns,
                "id desc");

        join(p, "pid", "parent");
        joinParentUser(p);
        userService.join(p, "user_id");

        return p;
    }

    private void joinParentUser(Page<SinglePageComment> p) {
        if (p == null || p.getList().isEmpty()) {
            return;
        }

        for (SinglePageComment pageComment : p.getList()) {
            userService.join((SinglePageComment) pageComment.get("parent"), "user_id");
        }
    }


    @Override
    public void doIncCommentReplyCount(long commentId) {
        Db.update("update single_page_comment set reply_count = reply_count + 1"
                + " where id = ? ", commentId);
    }

    @Override
    public boolean doChangeStatus(Long id, String status) {
        SinglePageComment comment = findById(id);
        comment.setStatus(status);
        return update(comment);
    }

    @Override
    public boolean batchChangeStatusByIds(String status, Object... ids) {
        Columns c = Columns.create().in("id", ids);
        Object[] paras = ArrayUtil.concat(new Object[]{status}, c.getValueArray());

        return Db.update("update single_page_comment SET `status` = ? " + SqlUtils.toWhereSql(c), paras) > 0;
    }
}