package io.jpress.module.shop.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.shop.model.PaymentRecord;

import java.util.List;

public interface PaymentRecordService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public PaymentRecord findById(Object id);


    /**
     * find all model
     *
     * @return all <PaymentRecord
     */
    public List<PaymentRecord> findAll();


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
    public boolean delete(PaymentRecord model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(PaymentRecord model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(PaymentRecord model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(PaymentRecord model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<PaymentRecord> paginate(int page, int pageSize);


}