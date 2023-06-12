package io.jpress.module.article.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.ArticleMetaInfo;

import java.util.List;

public interface ArticleMetaInfoService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    public ArticleMetaInfo findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 ArticleMetaInfo
     */
    public List<ArticleMetaInfo> findAll();


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
    public boolean delete(ArticleMetaInfo model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    public Object save(ArticleMetaInfo model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ArticleMetaInfo model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
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