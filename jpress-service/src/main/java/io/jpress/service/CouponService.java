package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.model.Coupon;

import java.util.List;

public interface CouponService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    Coupon findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 Coupon
     */
    List<Coupon> findAll();


    /**
     * 根据主键删除 Model
     *
     * @param id
     * @return success
     */
    boolean deleteById(Object id);


    boolean batchDeleteByIds(Object... ids);


    /**
     * 删除 Model
     *
     * @param model
     * @return
     */
    boolean delete(Coupon model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    Object save(Coupon model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    Object saveOrUpdate(Coupon model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(Coupon model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<Coupon> paginate(int page, int pageSize);

    Page<Coupon> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    void doSyncTakeCount(long couponId);

    void doSyncUsedCount(long couponId);


}
