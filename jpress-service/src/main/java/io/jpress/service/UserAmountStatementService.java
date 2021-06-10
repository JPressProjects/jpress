package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserAmountStatement;

import java.math.BigDecimal;
import java.util.List;

public interface UserAmountStatementService {

    /**
     * 根据 主键 查找 Model
     *
     * @param id
     * @return
     */
    UserAmountStatement findById(Object id);


    /**
     * 查询所有的数据
     *
     * @return 所有的 UserAmountStatement
     */
    List<UserAmountStatement> findAll();


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
    boolean delete(UserAmountStatement model);


    /**
     * 新增 Model 数据
     *
     * @param model
     * @return id value if save success
     */
    Object save(UserAmountStatement model);


    /**
     * 新增或者更新 Model 数据（主键值为 null 就新增，不为 null 则更新）
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    Object saveOrUpdate(UserAmountStatement model);


    /**
     * 更新此 Model 的数据，务必要保证此 Model 的主键不能为 null
     *
     * @param model
     * @return
     */
    boolean update(UserAmountStatement model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserAmountStatement> paginate(int page, int pageSize);


    List<UserAmountStatement> findListByUserId(Object userId, int count);


    BigDecimal queryIncomeAmount(Long id);

    BigDecimal queryPayAmount(Long id);

    BigDecimal queryPayoutAmount(Long id);

    UserAmountStatement findOneByUserIdAndRelative(Long userId, String relativeType, Long relativeId);
}