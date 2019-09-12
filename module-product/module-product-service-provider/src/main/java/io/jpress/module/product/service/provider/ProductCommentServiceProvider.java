package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.model.ProductComment;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductCommentServiceProvider extends JbootServiceBase<ProductComment> implements ProductCommentService {

}