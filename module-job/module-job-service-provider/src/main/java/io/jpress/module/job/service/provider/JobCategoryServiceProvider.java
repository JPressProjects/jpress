package io.jpress.module.job.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.model.JobCategory;
import io.jboot.service.JbootServiceBase;

@Bean
public class JobCategoryServiceProvider extends JbootServiceBase<JobCategory> implements JobCategoryService {

}