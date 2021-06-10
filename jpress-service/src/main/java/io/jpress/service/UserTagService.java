package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.UserTag;

import java.util.List;

public interface UserTagService {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    UserTag findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    UserTag findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    UserTag findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<UserTag> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<UserTag> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<UserTag> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<UserTag> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<UserTag> findListByColumns(Columns columns, String orderBy, Integer count);


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
    boolean delete(UserTag model);


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
    Object save(UserTag model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(UserTag model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(UserTag model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserTag> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserTag> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<UserTag> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    UserTag findFirstByTag(String tag);


    List<UserTag> findListByUserId(Object userId);


    List<UserTag> findOrCreateByTagString(String[] tags);

    List<UserTag> findHotList(int count);

    void doUpdateTags(long userId, Long[] tagIds);
}
