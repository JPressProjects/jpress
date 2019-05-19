package io.jpress.module.crawler.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jpress.module.crawler.model.Keyword;

import java.util.List;
import java.util.Map;

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
     * 分页查询数据(海量数据)
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<String> findListByPage(int pageNum, int pageSize);

    /**
     * 按条件查询待导出数据
     *
     * @date  2019-05-17 15:52
     * @param inputKeywords         输入关键词
     * @param categoryIds           关键词分类
     * @param validSearchTypes      有效关键词(搜索引擎)
     * @param checkedSearchTypes    已检核(搜索引擎)
     * @param minLength             最小长度
     * @param maxLength             最大长度
     * @param minNum                关键词最少个数
     * @param maxNum                关键词最多个数
     * @param orderBy
     * @return java.util.List<java.lang.String>
     */
    public List<String> findListByParams(String inputKeywords, String categoryIds, String validSearchTypes,
           String checkedSearchTypes, Integer minLength, Integer maxLength, Integer minNum, Integer maxNum, String orderBy);

    /**
     * 按分类查询关键词
     *
     * @param categoryId
     * @return
     */
    public List<Keyword> findListByCategoryId(Object categoryId);

    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);

    /**
     * delete models by categoryId
     *
     * @param categoryId
     * @return success
     */
    public boolean deleteByCategoryId(Object categoryId);

    /**
     * 批量删除关键词
     *
     * @date  2019-05-17 16:34
     * @param ids
     * @return boolean
     */
    public boolean deleteByIds(Object... ids);

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
     * batch save model to database
     *
     * @param keywordList
     * @param categoryId
     * @param categoryName
     * @return  boolean
     */
    public boolean batchSave(List<String> keywordList, Object categoryId, String categoryName);

    /**
     * batch save model to database
     *
     * @param keywordList
     * @param categoryList
     * @return  boolean
     */
    public boolean batchSave(List<Map<String, List<String>>> keywordList, List<String> categoryList);

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

    /**
     * 按分类查询关键词
     *
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    public Page<Keyword> paginate(int pageNum, int pageSize, Object categoryId);

    /**
     * 关键词分页查询
     *
     * @date  2019-05-17 15:52
     * @param page
     * @param pageSize
     * @param inputKeywords         输入关键词
     * @param categoryIds           关键词分类
     * @param validSearchTypes      有效关键词(搜索引擎)
     * @param checkedSearchTypes    已检核(搜索引擎)
     * @param minLength             最小长度
     * @param maxLength             最大长度
     * @param minNum                关键词最少个数
     * @param maxNum                关键词最多个数
     * @param orderBy
     * @return java.util.List<java.lang.String>
     */
    public Page<Keyword> paginate(int pageNum, int pageSize, String inputKeywords, String categoryIds, String validSearchTypes,
         String checkedSearchTypes, Integer minLength, Integer maxLength, Integer minNum, Integer maxNum, String orderBy);

    /**
     * 汇总所有分类关键词总数
     *
     * @param page
     * @param pageSize
     * @return
     */
    public List<Record> findAllCountByCategoryId();

    /**
     * 汇总单个分类关键词总数
     *
     * @param categoryId
     * @return
     */
    public Record findCountByCategoryId(Object categoryId);




}