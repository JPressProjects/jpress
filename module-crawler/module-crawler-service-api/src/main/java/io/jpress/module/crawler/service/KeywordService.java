package io.jpress.module.crawler.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.crawler.model.Keyword;

import java.util.List;

public interface KeywordService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Keyword findById(Object id);


    /**
     * find all model
     *
     * @return all <Keyword
     */
    public List<Keyword> findAll();


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
    public boolean delete(Keyword model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Keyword model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Keyword model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Keyword model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Keyword> paginate(int page, int pageSize);


}