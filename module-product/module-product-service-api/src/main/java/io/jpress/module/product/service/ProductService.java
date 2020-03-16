package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.product.model.Product;

import java.util.List;

public interface ProductService extends JbootServiceJoiner {

    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Product findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public Product findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public Product findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<Product> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<Product> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<Product> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<Product> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<Product> findListByColumns(Columns columns, String orderBy, Integer count);

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
    public boolean delete(Product model);


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
    public Object save(Product model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(Product model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(Product model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Product> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Product> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<Product> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

    public void removeCacheById(Object id);

    public void doUpdateCategorys(long productId, Long[] categoryIds);

    public void doUpdateCommentCount(long productId);

    public boolean doChangeStatus(long id, int status);

    public Page<Product> _paginateByStatus(int page, int pagesize, String title, Long categoryId, int status);

    public Page<Product> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId);

    public Page<Product> paginateInNormal(int page, int pagesize);

    public Page<Product> paginateInNormal(int page, int pagesize, String orderBy);

    public Page<Product> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy);

    public Product findFirstBySlug(String slug);

    public long findCountByStatus(int status);

    public void doIncProductViewCount(long productId);

    public void doIncProductCommentCount(long productId);

    public List<Product> findRelevantListByProductId(Long productId, int status, Integer count);

    public List<Product> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count);

    public Product findNextById(long id);

    public Product findPreviousById(long id);

    public Page<Product> search(String queryString, int pageNum, int pageSize);

    public Page<Product> searchIndb(String queryString, int pageNum, int pageSize);
}
