package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserOrderItem;

import java.util.List;

public interface UserOrderItemService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public UserOrderItem findById(Object id);


    /**
     * find all model
     *
     * @return all <UserOrderItem
     */
    public List<UserOrderItem> findAll();


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
    public boolean delete(UserOrderItem model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(UserOrderItem model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(UserOrderItem model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(UserOrderItem model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserOrderItem> paginate(int page, int pageSize);


    public void batchSave(List<UserOrderItem> userOrderItems);
}