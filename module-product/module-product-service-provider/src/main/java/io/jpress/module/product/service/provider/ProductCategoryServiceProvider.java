package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.model.ProductCategory;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductCategoryServiceProvider extends JbootServiceBase<ProductCategory> implements ProductCategoryService {

}