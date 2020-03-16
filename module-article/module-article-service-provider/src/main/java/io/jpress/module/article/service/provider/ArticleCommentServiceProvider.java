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
package io.jpress.module.article.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.ArrayUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.module.article.service.task.ArticleCommentReplyCountUpdateTask;
import io.jpress.service.UserService;

import java.util.List;

@Bean
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
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public boolean deleteById(Object id) {
        ArticleComment comment = findById(id);
        if (comment == null) {
            return false;
        }

        boolean delOK = super.deleteById(id);
        if (delOK && comment.getArticleId() != null) {
            articleService.doUpdateCommentCount(comment.getArticleId());
        }
        return delOK;
    }

    @Override
    public void deleteCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }

    @Override
    public boolean batchChangeStatusByIds(String status, Object... ids) {

        Columns c = Columns.create().in("id", ids);
        Object[] paras = ArrayUtil.concat(new Object[]{status}, c.getValueArray());

        return Db.update("update article_comment SET `status` = ? " + SqlUtils.toWhereSql(c), paras) > 0;
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
        return update(comment);
    }

    @Override
    public long findCountByStatus(String status) {
        return DAO.findCountByColumn(Column.create("status",status));
    }

    @Override
    public long findCountByArticleId(Long articleId) {
        return DAO.findCountByColumn(Column.create("article_id",articleId));
    }


    @Override
    public Page<ArticleComment> _paginateByStatus(int page, int pagesize, Long articleId, String keyword, String status) {

        Columns columns = Columns.create("article_id", articleId)
                .add("status", status)
                .likeAppendPercent("content", keyword);

        Page<ArticleComment> p = DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");

        userService.join(p, "user_id");
        articleService.join(p, "article_id");
        return p;
    }


    @Override
    public Page<ArticleComment> _paginateWithoutTrash(int page, int pagesize, Long articleId, String keyword) {

        Columns columns = Columns.create("article_id", articleId)
                .ne("status", ArticleComment.STATUS_TRASH)
                .likeAppendPercent("content", keyword);

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
        joinParentUser(p);
        userService.join(p, "user_id");

        return p;
    }

    private void joinParentUser(Page<ArticleComment> p) {
        if (p == null || p.getList().isEmpty()) {
            return;
        }

        for (ArticleComment articleComment : p.getList()) {
            userService.join((ArticleComment) articleComment.get("parent"), "user_id");
        }
    }


    @Override
    public void doIncCommentReplyCount(long commentId) {
        ArticleCommentReplyCountUpdateTask.recordCount(commentId);
    }


    @Override
    public boolean deleteByArticleId(Object articleId) {
        return DAO.deleteByColumn(Column.create("article_id",articleId));
    }


}