package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.product.service.ProductImageService;
import io.jpress.module.product.model.ProductImage;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductImageServiceProvider extends JbootServiceBase<ProductImage> implements ProductImageService {

}