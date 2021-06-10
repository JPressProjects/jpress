package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.MemberPrice;
import io.jboot.db.model.Columns;

import java.math.BigDecimal;
import java.util.List;

public interface MemberPriceService {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    MemberPrice findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    MemberPrice findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    MemberPrice findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    List<MemberPrice> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    List<MemberPrice> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    List<MemberPrice> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    List<MemberPrice> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    List<MemberPrice> findListByColumns(Columns columns, String orderBy, Integer count);


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
    boolean delete(MemberPrice model);


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
    Object save(MemberPrice model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    Object saveOrUpdate(MemberPrice model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    boolean update(MemberPrice model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<MemberPrice> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page<MemberPrice> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    Page<MemberPrice> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    MemberPrice findByPorductAndGroup(String productType, Object productId, Object groupId);

    void saveOrUpdateByProduct(String productType, Long productId, String[] memberGroupIds, String[] memberGroupPrices);

    BigDecimal queryPrice(String productType, Long productId, Long userId);


}