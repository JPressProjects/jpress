package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.product.service.ProductService;
import io.jpress.module.product.model.Product;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductServiceProvider extends JbootServiceBase<Product> implements ProductService {

}