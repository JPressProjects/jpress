package io.jpress.module.product.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.service.ProductService;
import io.jpress.module.product.service.provider.task.ProductCommentReplyCountUpdateTask;
import io.jpress.service.UserService;

@Bean
public class ProductCommentServiceProvider extends JbootServiceBase<ProductComment> implements ProductCommentService {

    @Inject
    private UserService userService;

    @Inject
    private ProductService productService;

    @Override
    public ProductComment findById(Object id) {
        ProductComment comment = super.findById(id);
        productService.join(comment, "product_id");
        userService.join(comment, "user_id");
        return comment;
    }

    @Override
    public long findCountByProductId(Long productId) {
        return DAO.findCountByColumn(Column.create("product_id",productId));
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public void deleteCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }

    @Override
    public long findCountByStatus(int status) {
        return DAO.findCountByColumn(Column.create("status",status));
    }

    @Override
    public Page<ProductComment> _paginateByStatus(int page, int pagesize, Long productId, String keyword, int status) {

        Columns columns = Columns.create("product_id", productId)
                .add("status", status)
                .likeAppendPercent("content", keyword);

        Page<ProductComment> p = DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");

        userService.join(p, "user_id");
        productService.join(p, "product_id");
        return p;
    }


    @Override
    public Page<ProductComment> _paginateWithoutTrash(int page, int pagesize, Long productId, String keyword) {

        Columns columns = Columns.create("product_id", productId)
                .ne("status", ProductComment.STATUS_TRASH)
                .likeAppendPercent("content", keyword);

        Page<ProductComment> p = DAO.paginateByColumns(
                page,
                pagesize,
                columns,
                "id desc");


        userService.join(p, "user_id");
        productService.join(p, "product_id");
        return p;
    }

    @Override
    public Page<ProductComment> _paginateByUserId(int page, int pagesize, long userId) {
        Page<ProductComment> p = DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), "id desc");
        userService.join(p, "user_id");
        productService.join(p, "product_id");
        return p;
    }

    @Override
    public Page<ProductComment> paginateByProductIdInNormal(int page, int pagesize, long productId) {
        Columns columns = Columns.create("product_id", productId);
        columns.add("status", ProductComment.STATUS_NORMAL);


        Page<ProductComment> p = DAO.paginateByColumns(
                page,
                pagesize,
                columns,
                "id desc");

        join(p, "pid", "parent");
        joinParentUser(p);
        userService.join(p, "user_id");

        return p;
    }

    @Override
    public void doIncCommentReplyCount(long commentId) {
        ProductCommentReplyCountUpdateTask.recordCount(commentId);
    }

    @Override
    public boolean doChangeStatus(Long id, int status) {
        ProductComment comment = findById(id);
        comment.setStatus(status);
        return update(comment);
    }

    private void joinParentUser(Page<ProductComment> p) {
        if (p == null || p.getList().isEmpty()) {
            return;
        }

        for (ProductComment articleComment : p.getList()) {
            userService.join((ProductComment) articleComment.get("parent"), "user_id");
        }
    }
}