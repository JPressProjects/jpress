package io.jpress.module.shop.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.shop.service.ProductCommentService;
import io.jpress.module.shop.model.ProductComment;
import io.jboot.service.JbootServiceBase;

@Bean
public class ProductCommentServiceProvider extends JbootServiceBase<ProductComment> implements ProductCommentService {

}