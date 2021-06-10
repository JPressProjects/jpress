package io.jpress.service;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.CouponCode;

import java.math.BigDecimal;
import java.util.List;

public interface CouponCodeService extends JbootServiceJoiner {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    CouponCode findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return all CouponCode
     */
    List<CouponCode> findAll();


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
    boolean delete(CouponCode model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    Object save(CouponCode model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    Object saveOrUpdate(CouponCode model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(CouponCode model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<CouponCode> paginate(int page, int pageSize);

    Page<CouponCode> paginateByCouponId(int page, int pageSize, Long couponId);


    CouponCode findByCode(String code);


    Ret valid(CouponCode couponCode, BigDecimal orderTotalAmount, long usedUserId);


    long queryCountByCouponId(long couponId);


    List<CouponCode> findAvailableByUserId(long userid, BigDecimal orderTotalAmount);

    List<CouponCode> findAvailableByUserId(long userid);

}
