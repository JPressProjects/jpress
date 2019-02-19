package io.jpress.addon.articlemeta.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.addon.articlemeta.model.ArticleMetaRecord;

import java.util.List;

public interface ArticleMetaRecordService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ArticleMetaRecord findById(Object id);


    /**
     * find model by articleId and fieldName
     * @param articleId
     * @param fieldName
     * @return
     */
    public ArticleMetaRecord findByArticleIdAndFieldName(Object articleId,String fieldName);


    /**
     * find all model
     *
     * @return all <ArticleMetaRecord
     */
    public List<ArticleMetaRecord> findAll();


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
    public boolean delete(ArticleMetaRecord model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(ArticleMetaRecord model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ArticleMetaRecord model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ArticleMetaRecord model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ArticleMetaRecord> paginate(int page, int pageSize);


}