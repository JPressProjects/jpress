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
    ProductCategory findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    ProductCategory findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    ProductCategory findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<ProductCategory> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<ProductCategory> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<ProductCategory> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<ProductCategory> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<ProductCategory> findListByColumns(Columns columns, String orderBy, Integer count);

    /**
     * 根据提交查询数据量
     *
     * @param columns
     * @return
     */
    long findCountByColumns(Columns columns);


    /**
     * 根据ID 删除model
     *
     * @param id
     * @return
     */
    boolean deleteById(Object id);


    /**
     * 删除
     *
     * @param model
     * @return
     */
    boolean delete(ProductCategory model);


    /**
     * 根据 多个 id 批量删除
     *
     * @param ids
     * @return
     */
    boolean batchDeleteByIds(Object... ids);


    /**
     * 保存到数据库
     *
     * @param model
     * @return id if success
     */
    Object save(ProductCategory model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(ProductCategory model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(ProductCategory model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<ProductCategory> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<ProductCategory> paginateByColumns(int page, int pageSize, Columns columns);

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<ProductCategory> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    Page<ProductCategory> paginateByType(int page, int pagesize, String type);


    List<ProductCategory> findListByProductId(long productId);

    List<ProductCategory> findCategoryListByProductId(long productId);

    List<ProductCategory> findTagListByProductId(long productId);

    List<ProductCategory> findListByProductId(long productId, String type);

    List<ProductCategory> findListByType(String type);

    List<ProductCategory> findListByType(String type, String orderBy, Integer count);

    List<ProductCategory> findOrCreateByTagString(String[] tags);
    List<ProductCategory> findOrCreateByCategoryString(String[] categories);


    ProductCategory findFirstByTypeAndSlug(String type, String slug);


    Long[] findCategoryIdsByProductId(long productId);


    void doUpdateProductCount(long categoryId);

    ProductCategory findFirstByFlag(String flag);


}