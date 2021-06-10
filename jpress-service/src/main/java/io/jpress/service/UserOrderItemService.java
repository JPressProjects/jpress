package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserOrderItem;

import java.util.List;

public interface UserOrderItemService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    UserOrderItem findById(Object id);


    boolean update(UserOrderItem item);

    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserOrderItem> paginate(int page, int pageSize);


    void batchSave(List<UserOrderItem> userOrderItems);

    List<UserOrderItem> findListByOrderId(Long orderId);

    boolean doAddProductCountById(Object id, Long userId);

    boolean doSubtractProductCountById(Object id, Long userId);

    boolean deleteById(Object id, Long userId);
}