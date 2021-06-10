package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.User;
import io.jpress.model.UserOpenid;
import io.jboot.db.model.Columns;

import java.util.List;

public interface UserOpenidService {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    UserOpenid findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    UserOpenid findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    UserOpenid findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<UserOpenid> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<UserOpenid> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<UserOpenid> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<UserOpenid> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<UserOpenid> findListByColumns(Columns columns, String orderBy, Integer count);


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
    boolean delete(UserOpenid model);


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
    Object save(UserOpenid model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(UserOpenid model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(UserOpenid model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserOpenid> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserOpenid> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<UserOpenid> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    User findByTypeAndOpenId(String type, String openId);

    boolean saveOrUpdate(Object userId, String type, String openId);

    UserOpenid findByUserIdAndType(Object userId, String type);

    void batchDeleteByUserId(Object userId);

    List<UserOpenid> findListByUserId(Object userId);


}