package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.product.model.ProductComment;

import java.util.List;


public interface ProductCommentService {

    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    ProductComment findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    ProductComment findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    ProductComment findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<ProductComment> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<ProductComment> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<ProductComment> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<ProductComment> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<ProductComment> findListByColumns(Columns columns, String orderBy, Integer count);

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
    boolean delete(ProductComment model);


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
    Object save(ProductComment model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(ProductComment model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(ProductComment model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<ProductComment> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<ProductComment> paginateByColumns(int page, int pageSize, Columns columns);

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<ProductComment> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    long findCountByProductId(Long productId);


    /**
     * batch del
     *
     * @param ids
     * @return
     */
    boolean deleteByIds(Object... ids);


    void deleteCacheById(Object id);

    /**
     * count
     *
     * @param status
     * @return
     */
    long findCountByStatus(int status);


    Page<ProductComment> _paginateByStatus(int page, int pagesize, Long productId, String keyword, int status);

    Page<ProductComment> _paginateWithoutTrash(int page, int pagesize, Long productId, String keyword);

    Page<ProductComment> _paginateByUserId(int page, int pagesize, long userId);

    Page<ProductComment> paginateByProductIdInNormal(int page, int pagesize, long productId);

    void doIncCommentReplyCount(long commentId);

    boolean doChangeStatus(Long id, int status);

    boolean deleteByProductId(Object productId);
}