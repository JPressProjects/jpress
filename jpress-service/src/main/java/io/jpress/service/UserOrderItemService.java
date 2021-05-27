package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserOrderItem;

import java.util.List;

public interface UserOrderItemService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public UserOrderItem findById(Object id);


    public boolean update(UserOrderItem item);
    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserOrderItem> paginate(int page, int pageSize);


    public void batchSave(List<UserOrderItem> userOrderItems);

    public List<UserOrderItem> findListByOrderId(Long orderId);

    public boolean doAddProductCountById(Object id, Long userId);

    public boolean doSubtractProductCountById(Object id, Long userId);

    public boolean deleteById(Object id,Long userId);
}