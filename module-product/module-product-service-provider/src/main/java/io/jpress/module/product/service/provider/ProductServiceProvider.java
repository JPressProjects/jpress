package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;

@Bean
public class ProductServiceProvider extends JbootServiceBase<Product> implements ProductService {

    @Override
    public boolean doChangeStatus(long id, String status) {
        Product product = findById(id);
        product.setStatus(status);
        return update(product);
    }
}