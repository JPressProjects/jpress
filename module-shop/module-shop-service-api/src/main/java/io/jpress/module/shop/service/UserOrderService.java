package io.jpress.module.shop.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.shop.model.UserOrder;

import java.util.List;

public interface UserOrderService  {

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


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserOrder> paginate(int page, int pageSize);


}