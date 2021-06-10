package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.UserAddress;

import java.util.List;

public interface UserAddressService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    UserAddress findById(Object id);


    /**
     * 根据条件查询
     *
     * @param column
     * @return
     */
    List<UserAddress> findListByColumns(Columns column);


    /**
     * 查询所有的数据
     *
     * @return 所有的 UserAddress
     */
    List<UserAddress> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(UserAddress model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    Object save(UserAddress model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    Object saveOrUpdate(UserAddress model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(UserAddress model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserAddress> paginate(int page, int pageSize, Long userId);

    UserAddress findDefaultAddress(long userId);

    List<UserAddress> findListByUserId(long userId);


    void addUserAddress(UserAddress address, long userid);
}
