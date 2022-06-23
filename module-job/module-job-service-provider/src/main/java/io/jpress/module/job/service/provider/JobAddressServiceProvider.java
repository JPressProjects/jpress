package io.jpress.module.job.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.module.job.service.JobAddressService;
import io.jpress.module.job.model.JobAddress;
import io.jboot.service.JbootServiceBase;

@Bean
public class JobAddressServiceProvider extends JbootServiceBase<JobAddress> implements JobAddressService {

}