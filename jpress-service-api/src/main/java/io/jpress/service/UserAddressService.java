package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.UserAddress;

import java.util.List;

public interface UserAddressService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public UserAddress findById(Object id);


    /**
     * 根据条件查询
     * @param column
     * @return
     */
    public List<UserAddress> findListByColumns(Columns column);


    /**
     * find all model
     *
     * @return all <UserAddress
     */
    public List<UserAddress> findAll();


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
    public boolean delete(UserAddress model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(UserAddress model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(UserAddress model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(UserAddress model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserAddress> paginate(int page, int pageSize,Long userId);

    public UserAddress findDefaultAddress(long userId);

    public List<UserAddress> findListByUserId(long userId);


    void addUserAddress(UserAddress address, long userid);
}
