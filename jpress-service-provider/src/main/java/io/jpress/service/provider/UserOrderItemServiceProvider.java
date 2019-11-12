package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.UserOrderItem;
import io.jpress.service.UserOrderItemService;

import java.util.List;

@Bean
public class UserOrderItemServiceProvider extends JbootServiceBase<UserOrderItem> implements UserOrderItemService {


    @Override
    public void batchSave(List<UserOrderItem> userOrderItems) {
        userOrderItems.forEach(this::save);
    }

    @Override
    public List<UserOrderItem> findListByOrderId(Long orderId) {
        return DAO.findListByColumn(Column.create("order_id", orderId));
    }

    @Override
    public boolean doAddProductCountById(Object id, Long userId) {
        return Db.update("update user_order_item set product_count = product_count + 1 "
                + " where id = ? && buyer_id = ?", id, userId) > 0;
    }

    @Override
    public boolean doSubtractProductCountById(Object id, Long userId) {
        return Db.update("update user_order_item set product_count = product_count - 1 "
                + " where id = ? && product_count > 1 && buyer_id = ?", id, userId) > 0;
    }

    @Override
    public boolean deleteById(Object id, Long userId) {
        return Db.delete("delete from user_order_item where id = ? && buyer_id = ?", id, userId) > 0;
    }
}