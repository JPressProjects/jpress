package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserAmountStatementService;
import io.jpress.model.UserAmountStatement;
import io.jboot.service.JbootServiceBase;

import java.util.List;

@Bean
public class UserAmountStatementServiceProvider extends JbootServiceBase<UserAmountStatement> implements UserAmountStatementService {

    @Override
    public List<UserAmountStatement> findListByUserId(Object userId, int count) {
        return DAO.findListByColumns(Columns.create("user_id", userId), "id desc",count);
    }
}