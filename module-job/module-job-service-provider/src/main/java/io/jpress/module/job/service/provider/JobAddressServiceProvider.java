package io.jpress.module.job.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.service.JobAddressService;

@Bean
public class JobAddressServiceProvider extends JPressServiceBase<JobAddress> implements JobAddressService {

}