package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.SiteInfo;
import io.jboot.db.model.Columns;

import java.util.List;

public interface SiteInfoService  {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    public SiteInfo findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public SiteInfo findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public SiteInfo findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<SiteInfo> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<SiteInfo> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<SiteInfo> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<SiteInfo> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<SiteInfo> findListByColumns(Columns columns, String orderBy, Integer count);


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
    public boolean delete(SiteInfo model);


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
    public Object save(SiteInfo model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(SiteInfo model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(SiteInfo model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<SiteInfo> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<SiteInfo> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<SiteInfo> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    /**
    * 储存 中间表信息
    *
    * @param siteId
    * @param roleIds
    * @return boolean
    */
    public boolean saveOrUpdateSiteRoleMapping(Long siteId,Long[] roleIds);
    
    /**
    * 查询是否已经有多语言默认站点
    *
    * @return boolean
    */
    public SiteInfo isHasLangDefault();


}