package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.UserCart;
import io.jpress.service.UserCartService;

import java.util.List;

@Bean
public class UserCartServiceProvider extends JbootServiceBase<UserCart> implements UserCartService {

    @Override
    public Object save(UserCart model) {
        UserCart userCart = findByProductTypeAndProductId(model.getProductType(), model.getProductId());
        if (userCart == null) {
            return super.save(model);
        } else {
            userCart.setProductCount(userCart.getProductCount() + 1);
        }
        update(userCart);
        return userCart.getId();
    }

    @Override
    public List<UserCart> findListByUserId(Object userId, int count) {
        return DAO.findListByColumns(Columns.create("user_id", userId), "id desc", count);
    }

    @Override
    public long findCountByUserId(Object userId) {
        return DAO.findCountByColumn(Column.create("user_id", userId));
    }

    @Override
    public List<UserCart> findSelectedListByUserId(Long userId) {
        return DAO.findListByColumns(Columns.create("user_id", userId).eq("selected", true), "id desc");
    }

    @Override
    public boolean doAddCountById(Object id) {
        return Db.update("update user_cart set product_count = product_count + 1 "
                + " where id = ? ", id) > 0;
    }

    @Override
    public boolean doSubtractCountById(Object id) {
        return Db.update("update user_cart set product_count = product_count - 1 "
                + " where id = ? && product_count > 1", id) > 0;
    }

    @Override
    public UserCart findByProductTypeAndProductId(String productType, long productId) {
        return DAO.findFirstByColumns(Columns.create("product_type", productType).eq("product_id", productId));
    }

    @Override
    public Page<UserCart> paginateByUser(int page, int pageSize, Long userId) {
        return paginateByColumns(page,pageSize,Columns.create("user_id",userId),"id desc");
    }

    @Override
    public long querySelectCount(Long userId) {
//        return JbootDb.queryInt("select count(*) from ");
        return DAO.findCountByColumns(Columns.create("user_id",userId).eq("selected",true));
    }
}