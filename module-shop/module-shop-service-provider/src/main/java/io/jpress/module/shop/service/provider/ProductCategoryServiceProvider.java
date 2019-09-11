package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.ProductCategoryService;
import io.jpress.module.shop.model.ProductCategory;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductCategoryServiceProvider extends JbootServiceBase<ProductCategory> implements ProductCategoryService {

}