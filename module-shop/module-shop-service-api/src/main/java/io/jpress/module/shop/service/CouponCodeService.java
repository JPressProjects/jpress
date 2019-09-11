package io.jpress.module.shop.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.shop.model.CouponCode;

import java.util.List;

public interface CouponCodeService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public CouponCode findById(Object id);


    /**
     * find all model
     *
     * @return all <CouponCode
     */
    public List<CouponCode> findAll();


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
    public boolean delete(CouponCode model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(CouponCode model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(CouponCode model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(CouponCode model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<CouponCode> paginate(int page, int pageSize);


}