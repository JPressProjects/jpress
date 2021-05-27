package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.product.model.ProductCategory;

import java.util.List;

public interface ProductCategoryService {

    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public ProductCategory findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public ProductCategory findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public ProductCategory findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<ProductCategory> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<ProductCategory> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<ProductCategory> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<ProductCategory> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<ProductCategory> findListByColumns(Columns columns, String orderBy, Integer count);

    /**
     * 根据提交查询数据量
     *
     * @param columns
     * @return
     */
    public long findCountByColumns(Columns columns);


    /**
     * 根据ID 删除model
     *
     * @param id
     * @return
     */
    public boolean deleteById(Object id);


    /**
     * 删除
     *
     * @param model
     * @return
     */
    public boolean delete(ProductCategory model);


    /**
     * 根据 多个 id 批量删除
     *
     * @param ids
     * @return
     */
    public boolean batchDeleteByIds(Object... ids);


    /**
     * 保存到数据库
     *
     * @param model
     * @return id if success
     */
    public Object save(ProductCategory model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(ProductCategory model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(ProductCategory model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductCategory> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductCategory> paginateByColumns(int page, int pageSize, Columns columns);

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<ProductCategory> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    public Page<ProductCategory> paginateByType(int page, int pagesize, String type);


    public List<ProductCategory> findListByProductId(long productId);

    public List<ProductCategory> findCategoryListByProductId(long productId);

    public List<ProductCategory> findTagListByProductId(long productId);

    public List<ProductCategory> findListByProductId(long productId, String type);

    public List<ProductCategory> findListByType(String type);

    public List<ProductCategory> findListByType(String type,String orderBy,Integer count);

    public List<ProductCategory> findOrCreateByTagString(String[] tags);


    public ProductCategory findFirstByTypeAndSlug(String type, String slug);


    public Long[] findCategoryIdsByProductId(long productId);


    public void doUpdateProductCount(long categoryId);

    public ProductCategory findFirstByFlag(String flag);


}