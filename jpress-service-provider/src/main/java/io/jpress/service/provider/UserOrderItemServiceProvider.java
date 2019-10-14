package io.jpress.service.provider;

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
        userOrderItems.forEach(userOrderItem -> save(userOrderItem));
    }

    @Override
    public List<UserOrderItem> findListByOrderId(Long orderId) {
        return DAO.findListByColumn(Column.create("order_id",orderId));
    }
}