package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserAmountPayoutService;
import io.jpress.model.UserAmountPayout;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserAmountPayoutServiceProvider extends JbootServiceBase<UserAmountPayout> implements UserAmountPayoutService {

    @Override
    public Page<UserAmountPayout> paginateByUserId(int page, int pageSize, Object userId) {
        return paginateByColumns(page, pageSize, Columns.create("user_id", userId), "id desc");
    }
}