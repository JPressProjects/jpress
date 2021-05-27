package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.UserAmountStatement;

import java.math.BigDecimal;
import java.util.List;

public interface UserAmountStatementService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public UserAmountStatement findById(Object id);


    /**
     * find all model
     *
     * @return all <UserAmountStatement
     */
    public List<UserAmountStatement> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(UserAmountStatement model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(UserAmountStatement model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(UserAmountStatement model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(UserAmountStatement model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<UserAmountStatement> paginate(int page, int pageSize);


    public List<UserAmountStatement> findListByUserId(Object userId, int count);


    public BigDecimal queryIncomeAmount(Long id);

    public BigDecimal queryPayAmount(Long id);

    public BigDecimal queryPayoutAmount(Long id);

    public UserAmountStatement findOneByUserIdAndRelative(Long userId,String relativeType,Long relativeId);
}