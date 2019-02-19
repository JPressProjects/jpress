package io.jpress.addon.articlemeta.service;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.addon.articlemeta.model.ArticleMetaInfo;

import java.util.List;

public interface ArticleMetaInfoService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ArticleMetaInfo findById(Object id);


    /**
     * find all model
     *
     * @return all <ArticleMetaInfo
     */
    public List<ArticleMetaInfo> findAll();


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
    public boolean delete(ArticleMetaInfo model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(ArticleMetaInfo model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ArticleMetaInfo model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ArticleMetaInfo model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ArticleMetaInfo> paginate(int page, int pageSize);


}