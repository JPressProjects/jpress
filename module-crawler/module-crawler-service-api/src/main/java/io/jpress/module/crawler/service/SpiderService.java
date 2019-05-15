package io.jpress.module.crawler.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.crawler.model.Spider;

import java.util.List;

public interface SpiderService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Spider findById(Object id);


    /**
     * find all model
     *
     * @return all <Spider
     */
    public List<Spider> findAll();


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
    public boolean delete(Spider model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Spider model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Spider model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Spider model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Spider> paginate(int page, int pageSize);


}