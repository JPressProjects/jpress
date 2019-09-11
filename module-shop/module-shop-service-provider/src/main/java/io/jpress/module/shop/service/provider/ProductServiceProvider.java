package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.ProductService;
import io.jpress.module.shop.model.Product;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductServiceProvider extends JbootServiceBase<Product> implements ProductService {

}