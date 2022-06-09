package io.jpress.module.page.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.model.SinglePageCategory;
import io.jboot.service.JbootServiceBase;

@Bean
public class SinglePageCategoryServiceProvider extends JbootServiceBase<SinglePageCategory> implements SinglePageCategoryService {

}