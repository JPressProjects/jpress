package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.UserOrder;
import io.jpress.model.UserOrderItem;

import java.util.List;

public interface UserOrderService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public UserOrder findById(Object id);


    /**
     * find all model
     *
     * @return all <UserOrder
     */
    public List<UserOrder> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(UserOrder model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(UserOrder model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(UserOrder model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(UserOrder model);

    public boolean updateOrderAndItems(UserOrder order, List<UserOrderItem> items);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserOrder> paginate(int page, int pageSize);


    public Page<UserOrder> paginate(int page, int pageSize, String title, String ns);

    public Page<UserOrder> paginateByUserId(int page, int pageSize,long userid, String title, String ns);

    public UserOrder findByPaymentId(Long id);

    public int queryTotayCount();

    public int queryMonthCount();

    public int queryMonthUserCount();
}