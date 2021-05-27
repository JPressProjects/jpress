package io.jpress.module.page.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.page.model.SinglePageComment;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SinglePageCommentService  {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    public SinglePageComment findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public SinglePageComment findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public SinglePageComment findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<SinglePageComment> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<SinglePageComment> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<SinglePageComment> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<SinglePageComment> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<SinglePageComment> findListByColumns(Columns columns, String orderBy, Integer count);


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
    public boolean delete(SinglePageComment model);


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
    public Object save(SinglePageComment model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(SinglePageComment model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(SinglePageComment model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<SinglePageComment> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<SinglePageComment> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<SinglePageComment> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

    public long findCountByStatus(String status);

    public Page<SinglePageComment> _paginateByStatus(int page, int pagesize, Long articleId, String keyword, String status);

    public Page<SinglePageComment> _paginateWithoutTrash(int page, int pagesize, Long articleId, String keyword);

    public Page<SinglePageComment> paginateByPageIdInNormal(int page, int pagesize, long pageId);

    public void doIncCommentReplyCount(long commentId);


    public boolean doChangeStatus(Long id, String status);

    public boolean batchChangeStatusByIds(String statusNormal, Object... toArray);
}