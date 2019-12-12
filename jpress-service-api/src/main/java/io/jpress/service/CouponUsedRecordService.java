package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.CouponUsedRecord;

import java.util.List;

public interface CouponUsedRecordService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public CouponUsedRecord findById(Object id);


    /**
     * find all model
     *
     * @return all <CouponUsedRecord
     */
    public List<CouponUsedRecord> findAll();


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
    public boolean delete(CouponUsedRecord model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(CouponUsedRecord model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(CouponUsedRecord model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(CouponUsedRecord model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<CouponUsedRecord> paginate(int page, int pageSize, Columns columns);


    public long queryCountByCouponId(long couponId);
}