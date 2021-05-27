package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.UserTag;

import java.util.List;

public interface UserTagService  {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    public UserTag findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public UserTag findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public UserTag findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<UserTag> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<UserTag> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<UserTag> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<UserTag> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<UserTag> findListByColumns(Columns columns, String orderBy, Integer count);


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
    public boolean delete(UserTag model);


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
    public Object save(UserTag model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(UserTag model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(UserTag model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserTag> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserTag> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<UserTag> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    public UserTag findFirstByTag(String tag);


    public List<UserTag> findListByUserId(Object userId);


    public List<UserTag> findOrCreateByTagString(String[] tags);

    public List<UserTag> findHotList(int count);

    public void doUpdateTags(long userId, Long[] tagIds);
}
