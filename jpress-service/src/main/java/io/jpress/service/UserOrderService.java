package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;

import java.util.List;

public interface UserOrderService extends JbootServiceJoiner {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    UserOrder findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 UserOrder
     */
    List<UserOrder> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(UserOrder model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    Object save(UserOrder model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    Object saveOrUpdate(UserOrder model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(UserOrder model);

    boolean updateOrderAndItems(UserOrder order, List<UserOrderItem> items);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserOrder> paginate(int page, int pageSize);


    Page<UserOrder> paginate(int page, int pageSize, String title, String ns);

    Page<UserOrder> paginateByUserId(int page, int pageSize, long userid, String title, String ns);

    UserOrder findByPaymentId(Long id);

    int queryTotayCount();

    int queryMonthCount();

    int queryMonthUserCount();
}