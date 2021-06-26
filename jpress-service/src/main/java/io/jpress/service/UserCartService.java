package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserCart;

import java.util.List;

public interface UserCartService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    UserCart findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 UserCart
     */
    List<UserCart> findAll();

    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(UserCart model);


    boolean deleteById(Object id);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    Object save(UserCart model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    Object saveOrUpdate(UserCart model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(UserCart model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserCart> paginate(int page, int pageSize);

    List<UserCart> findListByUserId(Object userId);

    long findCountByUserId(Object userId);

    List<UserCart> findSelectedListByUserId(Long id);

    UserCart findByProductInfo(long userId, String productType, long productId, String productSpec);

    Page<UserCart> paginateByUser(int page, int pageSize, Long userId);

    long querySelectedCount(Long userId);
}