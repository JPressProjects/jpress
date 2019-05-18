package io.jpress.module.crawler.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.crawler.model.KeywordCategory;

import java.util.List;

public interface KeywordCategoryService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public KeywordCategory findById(Object id);


    /**
     * find all model
     *
     * @return all <KeywordCategory
     */
    public List<KeywordCategory> findAll();

    /**
     * find model by name
     * @param name
     * @return KeywordCategory
     */
    public KeywordCategory findByName(String name);


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
    public boolean delete(KeywordCategory model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(KeywordCategory model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(KeywordCategory model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(KeywordCategory model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<KeywordCategory> paginate(int page, int pageSize);


}