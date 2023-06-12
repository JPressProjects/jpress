package io.jpress.module.article.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.ArticleMetaRecord;

import java.util.List;

public interface ArticleMetaRecordService  {

    /**
     * 根据 主键 查找 Model
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
     * 查询所有的数据
     *
     * @return 所有的 ArticleMetaRecord
     */
    public List<ArticleMetaRecord> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    public boolean delete(ArticleMetaRecord model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(ArticleMetaRecord model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ArticleMetaRecord model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
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


    public void batchSaveOrUpdate(List<ArticleMetaRecord> records);
}