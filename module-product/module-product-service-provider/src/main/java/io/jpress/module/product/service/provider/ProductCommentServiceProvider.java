package io.jpress.module.product.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.service.JbootServiceBase;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.service.ProductCommentService;

@Bean
public class ProductCommentServiceProvider extends JbootServiceBase<ProductComment> implements ProductCommentService {

}