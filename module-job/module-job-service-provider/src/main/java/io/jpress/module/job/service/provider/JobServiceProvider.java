package io.jpress.module.job.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.job.service.JobService;
import io.jpress.module.job.model.Job;
import io.jboot.service.JbootServiceBase;

@Bean
public class JobServiceProvider extends JbootServiceBase<Job> implements JobService {

}