package io.jpress.module.job.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.job.service.JobDepartmentService;
import io.jpress.module.job.model.JobDepartment;
import io.jboot.service.JbootServiceBase;

@Bean
public class JobDepartmentServiceProvider extends JbootServiceBase<JobDepartment> implements JobDepartmentService {

}