package io.jpress.module.page.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.page.model.SinglePageComment;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SinglePageCommentService {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    SinglePageComment findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    SinglePageComment findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    SinglePageComment findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<SinglePageComment> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<SinglePageComment> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<SinglePageComment> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<SinglePageComment> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<SinglePageComment> findListByColumns(Columns columns, String orderBy, Integer count);


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
    boolean delete(SinglePageComment model);


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
    Object save(SinglePageComment model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(SinglePageComment model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(SinglePageComment model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<SinglePageComment> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<SinglePageComment> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<SinglePageComment> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

    long findCountByStatus(String status);

    Page<SinglePageComment> _paginateByStatus(int page, int pagesize, Long articleId, String keyword, String status);

    Page<SinglePageComment> _paginateWithoutTrash(int page, int pagesize, Long articleId, String keyword);

    Page<SinglePageComment> paginateByPageIdInNormal(int page, int pagesize, long pageId);

    void doIncCommentReplyCount(long commentId);


    boolean doChangeStatus(Long id, String status);

    boolean batchChangeStatusByIds(String statusNormal, Object... toArray);

    long findCountByPageId(long pageId);

}