package io.jpress.module.page.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.module.page.model.SinglePageComment;
import io.jboot.service.JbootServiceBase;

@Bean
public class SinglePageCommentServiceProvider extends JbootServiceBase<SinglePageComment> implements SinglePageCommentService {

}