package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserCartService;
import io.jpress.model.UserCart;
import io.jboot.service.JbootServiceBase;

import java.util.List;

@Bean
public class UserCartServiceProvider extends JbootServiceBase<UserCart> implements UserCartService {


    @Override
    public List<UserCart> findListByUserId(Object userId, int count) {
        return DAO.findListByColumns(Columns.create("user_id", userId), "id desc", count);
    }

    @Override
    public List<UserCart> findSelectedByUserId(Long userId) {
        return DAO.findListByColumns(Columns.create("user_id", userId).eq("select_status",true), "id desc");
    }
}