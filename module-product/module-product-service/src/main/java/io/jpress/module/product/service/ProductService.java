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
    Product findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    Product findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    Product findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<Product> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<Product> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<Product> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<Product> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<Product> findListByColumns(Columns columns, String orderBy, Integer count);

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
    boolean delete(Product model);


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
    Object save(Product model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(Product model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(Product model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<Product> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<Product> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<Product> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

    void removeCacheById(Object id);

    void doUpdateCategorys(long productId, Long[] categoryIds);

    void doUpdateCommentCount(long productId);

    boolean doChangeStatus(long id, int status);

    Page<Product> _paginateByStatus(int page, int pagesize, String title, Long categoryId, int status);

    Page<Product> _paginateWithoutTrash(int page, int pagesize, String title, Long categoryId);

    Page<Product> paginateInNormal(int page, int pagesize);

    Page<Product> paginateInNormal(int page, int pagesize, String orderBy);

    Page<Product> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy);

    Product findFirstBySlug(String slug);

    long findCountByStatus(int status);

    void doIncProductViewCount(long productId);

    void doIncProductCommentCount(long productId);

    List<Product> findRelevantListByProductId(Long productId, int status, Integer count);

    List<Product> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count);

    Product findNextById(long id);

    Product findPreviousById(long id);

    Page<Product> search(String queryString, int pageNum, int pageSize);

    Page<Product> searchIndb(String queryString, int pageNum, int pageSize);
}
