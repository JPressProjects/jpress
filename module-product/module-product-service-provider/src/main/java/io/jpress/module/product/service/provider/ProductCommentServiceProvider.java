package io.jpress.module.product.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCommentService;

@Bean
public class ProductCommentServiceProvider extends JbootServiceBase<ProductComment> implements ProductCommentService {

    @Override
    public long findCountByProductId(Long productId) {
        return Db.queryLong("select count(*) from product_comment where product_id = ?", productId);
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public long findCountByStatus(String status) {
        return Db.queryLong("select count(*) from product_comment where status = ?", status);
    }
}