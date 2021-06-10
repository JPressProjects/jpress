package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserFavorite;
import io.jboot.db.model.Columns;

import java.util.List;

public interface UserFavoriteService {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    UserFavorite findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    UserFavorite findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    UserFavorite findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<UserFavorite> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<UserFavorite> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<UserFavorite> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<UserFavorite> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<UserFavorite> findListByColumns(Columns columns, String orderBy, Integer count);


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
    boolean delete(UserFavorite model);


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
    Object save(UserFavorite model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(UserFavorite model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(UserFavorite model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserFavorite> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserFavorite> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<UserFavorite> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    Page<UserFavorite> paginateByUserIdAndType(int pagePara, int pagesize, Long id, String type);

    boolean doDelFavoriteByType(String type, Long userid, Long id);

    boolean doDelFavorite(Long id);

    boolean doAddToFavorite(UserFavorite favorite);

    boolean isFav(Long userId, String type, Long id);

    boolean isProductFav(Long userId, Long id);

    boolean isArticleFav(Long userId, Long id);


}
