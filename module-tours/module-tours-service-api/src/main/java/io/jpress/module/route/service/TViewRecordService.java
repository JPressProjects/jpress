package io.jpress.module.route.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.route.model.TViewRecord;

import java.util.List;

public interface TViewRecordService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public TViewRecord findById(Object id);


    /**
     * find all model
     *
     * @return all <TViewRecord
     */
    public List<TViewRecord> findAll();


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
    public boolean delete(TViewRecord model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(TViewRecord model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(TViewRecord model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(TViewRecord model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<TViewRecord> paginate(int page, int pageSize);


}