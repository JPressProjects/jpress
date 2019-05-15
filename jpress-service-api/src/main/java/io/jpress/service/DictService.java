package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.Dict;

import java.util.List;

public interface DictService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Dict findById(Object id);


    /**
     * find all model
     *
     * @return all <Dict
     */
    public List<Dict> findAll();

    /**
     * find by type id
     * @param type
     * @return all <Dict
     */
    public List<Dict> findByType(String type);


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);

    /**
     * delete model by ids
     *
     * @param ids
     * @return success
     */
    public boolean deleteByIds(Object ids);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Dict model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Dict model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Dict model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Dict model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Dict> paginate(int page, int pageSize);


}