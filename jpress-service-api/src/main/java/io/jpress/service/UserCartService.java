package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserCart;

import java.util.List;

public interface UserCartService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public UserCart findById(Object id);


    /**
     * find all model
     *
     * @return all <UserCart
     */
    public List<UserCart> findAll();

    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(UserCart model);


    /**
     * save model to database
     *
     * @param model
     * @return id value if save success
     */
    public Object save(UserCart model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(UserCart model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(UserCart model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserCart> paginate(int page, int pageSize);

    public List<UserCart> findListByUserId(Object userId);

    public long findCountByUserId(Object userId);

    public List<UserCart> findSelectedListByUserId(Long id);

    public UserCart findByProductInfo(long userId, String productType, long productId, String productSpec);

    public Page<UserCart> paginateByUser(int page, int pageSize, Long userId);

    public long querySelectedCount(Long userId);
}