package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.product.model.ProductComment;

import java.util.List;


public interface ProductCommentService  {

    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public ProductComment findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public ProductComment findFirstByColumns(Columns columns);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public ProductComment findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<ProductComment> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<ProductComment> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<ProductComment> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<ProductComment> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<ProductComment> findListByColumns(Columns columns, String orderBy, Integer count);

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
    public boolean delete(ProductComment model);


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
    public Object save(ProductComment model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(ProductComment model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(ProductComment model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductComment> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductComment> paginateByColumns(int page, int pageSize, Columns columns);

    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<ProductComment> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    public long findCountByProductId(Long productId);


    /**
     * batch del
     * @param ids
     * @return
     */
    public boolean deleteByIds(Object... ids);


    public void deleteCacheById(Object id);
    /**
     * count
     * @param status
     * @return
     */
    public long findCountByStatus(int status);


    public Page<ProductComment> _paginateByStatus(int page, int pagesize, Long productId, String keyword, int status);

    public Page<ProductComment> _paginateWithoutTrash(int page, int pagesize, Long productId, String keyword);

    public Page<ProductComment> _paginateByUserId(int page, int pagesize, long userId);

    public Page<ProductComment> paginateByProductIdInNormal(int page, int pagesize, long productId);

    public void doIncCommentReplyCount(long commentId);

    public boolean doChangeStatus(Long id, int status);
}