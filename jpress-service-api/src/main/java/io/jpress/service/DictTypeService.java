package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.DictType;

import java.util.List;

public interface DictTypeService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public DictType findById(Object id);


    /**
     * find all model
     *
     * @return all <DictType
     */
    public List<DictType> findAll();


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
    public boolean delete(DictType model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(DictType model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(DictType model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(DictType model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<DictType> paginate(int page, int pageSize);


}