package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
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
        return DAO.findListByColumns(Columns.create("user_id", userId).eq("selected",true), "id desc");
    }

    @Override
    public boolean doAddCountById(Object id) {
        return Db.update("update user_cart set product_count = product_count + 1 "
                + " where id = ? ", id) > 0;
    }

    @Override
    public boolean doSubtractCountById(Object id) {
        return Db.update("update user_cart set product_count = product_count - 1 "
                + " where id = ? ", id) > 0;
    }
}