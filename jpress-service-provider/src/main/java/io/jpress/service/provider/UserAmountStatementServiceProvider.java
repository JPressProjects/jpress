package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jpress.service.UserAmountStatementService;
import io.jpress.model.UserAmountStatement;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserAmountStatementServiceProvider extends JbootServiceBase<UserAmountStatement> implements UserAmountStatementService {

}