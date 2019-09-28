package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jpress.module.product.service.ProductImageService;
import io.jpress.module.product.model.ProductImage;
import io.jboot.service.JbootServiceBase;

import java.util.List;

@Bean
public class ProductImageServiceProvider extends JbootServiceBase<ProductImage> implements ProductImageService {

    @Override
    public List<ProductImage> findListByProductId(Object productId) {
        return DAO.findListByColumn(Column.create("product_id",productId));
    }
}