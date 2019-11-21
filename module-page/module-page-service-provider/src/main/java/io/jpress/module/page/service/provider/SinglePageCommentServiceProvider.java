package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.module.page.model.SinglePageComment;
import io.jboot.service.JbootServiceBase;

@Bean
public class SinglePageCommentServiceProvider extends JbootServiceBase<SinglePageComment> implements SinglePageCommentService {

    @Override
    public long findCountByStatus(String status) {
        return 0;
    }

    @Override
    public Page<SinglePageComment> _paginateByStatus(int page, int pagesize, Long articleId, String keyword, String status) {
        return null;
    }

    @Override
    public Page<SinglePageComment> _paginateWithoutTrash(int page, int pagesize, Long articleId, String keyword) {
        return null;
    }

    @Override
    public Page<SinglePageComment> _paginateByUserId(int page, int pagesize, long userId) {
        return null;
    }

    @Override
    public Page<SinglePageComment> paginateByArticleIdInNormal(int page, int pagesize, long articleId) {
        return null;
    }

    @Override
    public void doIncCommentReplyCount(long commentId) {

    }
}