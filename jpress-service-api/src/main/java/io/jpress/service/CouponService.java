package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.Coupon;

import java.util.List;

public interface CouponService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Coupon findById(Object id);


    /**
     * find all model
     *
     * @return all <Coupon
     */
    public List<Coupon> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    public boolean batchDeleteByIds(Object... ids);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Coupon model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Coupon model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Coupon model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Coupon model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Coupon> paginate(int page, int pageSize);


    public void doSyncTakeCount(long couponId);

    public void doSyncUsedCount(long couponId);


}