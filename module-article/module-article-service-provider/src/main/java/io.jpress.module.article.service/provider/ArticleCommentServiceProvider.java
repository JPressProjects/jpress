package io.jpress.module.article.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtils;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.task.CommentReplayCountUpdateTask;
import io.jpress.service.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class ArticleCommentServiceProvider extends JbootServiceBase<ArticleComment> implements ArticleCommentService {

    @Inject
    private ArticleService articleService;

    @Inject
    private UserService userService;


    @Override
    public ArticleComment findById(Object id) {
        ArticleComment comment = super.findById(id);
        articleService.join(comment, "article_id");
        userService.join(comment, "user_id");
        return comment;
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        return Db.update("delete from article_comment where id in  " + SqlUtils.buildInSqlPara(ids)) > 0;
    }

    @Override
    public boolean batchChangeStatusByIds(String status, Object... ids) {
        return Db.update("update article_comment SET `status` = ? where id in  " + SqlUtils.buildInSqlPara(ids), status) > 0;
    }

    @Override
    public Page<ArticleComment> paginate(int page, int pageSize) {
        Page p = super.paginate(page, pageSize);
        articleService.join(p, "article_id");
        userService.join(p, "user_id");
        return p;
    }


    @Override
    public List<ArticleComment> findListByColumns(Columns columns, String orderBy, Integer count) {
        List<ArticleComment> list = DAO.findListByColumns(columns, orderBy, count);
        articleService.join(list, "article_id");
        userService.join(list, "user_id");
        return list;
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        ArticleComment comment = findById(id);
        comment.setStatus(status);
        return comment.update();
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from article_comment where status = ?", status);
    }


    @Override
    public Page<ArticleComment> _paginateByStatus(int page, int pagesize, String keyword, String status) {

        Columns columns = Columns.create("status", status);
        SqlUtils.likeAppend(columns, "content", keyword);

        Page<ArticleComment> p = DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");
        userService.join(p, "user_id");
        articleService.join(p, "article_id");
        return p;
    }

    @Override
    public Page<ArticleComment> _paginateWithoutTrash(int page, int pagesize, String keyword) {

        Columns columns = Columns.create(Column.create("status", ArticleComment.STATUS_TRASH, Column.LOGIC_NOT_EQUALS));
        if (StrUtils.isNotBlank(keyword)) {
            columns.like("content", "%" + keyword + "%");
        }

        Page<ArticleComment> p = DAO.paginateByColumns(
                page,
                pagesize,
                columns,
                "id desc");


        userService.join(p, "user_id");
        articleService.join(p, "article_id");
        return p;
    }

    @Override
    public Page<ArticleComment> _paginateByUserId(int page, int pagesize, long userId) {
        Page<ArticleComment> p = DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), "id desc");
        articleService.join(p, "article_id");
        return p;
    }

    @Override
    public Page<ArticleComment> paginateByArticleIdInNormal(int page, int pagesize, long articleId) {
        Columns columns = Columns.create("article_id", articleId);
        columns.add("status", ArticleComment.STATUS_NORMAL);


        Page<ArticleComment> p = DAO.paginateByColumns(
                page,
                pagesize,
                columns,
                "id desc");

        join(p, "pid", "parent");
        userService.join(p, "user_id");
        return p;
    }

    @Override
    public void doIncCommentReplayCount(long commentId) {
        CommentReplayCountUpdateTask.recordCount(commentId);
    }


}