package io.jpress.module.crawler.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.crawler.model.Webpage;

import java.util.List;

public interface WebpageService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Webpage findById(Object id);


    /**
     * find all model
     *
     * @return all <Webpage
     */
    public List<Webpage> findAll();


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
    public boolean delete(Webpage model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Webpage model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Webpage model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Webpage model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Webpage> paginate(int page, int pageSize);


}