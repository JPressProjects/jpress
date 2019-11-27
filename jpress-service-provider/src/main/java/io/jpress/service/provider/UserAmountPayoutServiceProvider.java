package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserAmountPayoutService;
import io.jpress.model.UserAmountPayout;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.UserService;

@Bean
public class UserAmountPayoutServiceProvider extends JbootServiceBase<UserAmountPayout> implements UserAmountPayoutService {

    @Inject
    private UserService userService;

    @Override
    public Page<UserAmountPayout> paginateByUserId(int page, int pageSize, Object userId, Integer status) {
        return userService.join(paginateByColumns(page, pageSize, Columns.create("user_id", userId).eq("status",status), "id desc"), "user_id");
    }
}