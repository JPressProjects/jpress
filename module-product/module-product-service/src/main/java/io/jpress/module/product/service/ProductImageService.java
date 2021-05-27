package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.product.model.ProductImage;

import java.util.List;

public interface ProductImageService  {

    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public ProductImage findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public ProductImage findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public ProductImage findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<ProductImage> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<ProductImage> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<ProductImage> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<ProductImage> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<ProductImage> findListByColumns(Columns columns, String orderBy, Integer count);

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
    public boolean delete(ProductImage model);


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
    public Object save(ProductImage model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(ProductImage model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(ProductImage model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductImage> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductImage> paginateByColumns(int page, int pageSize, Columns columns);

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<ProductImage> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

    public List<ProductImage> findListByProductId(Long productId);

    public void saveOrUpdateByProductId(Long id, String[] imageIds, String[] imageSrcs);

    public boolean deleteByProductId(Long productId);
}