package io.jpress.module.job.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.job.model.JobCategory;
import io.jboot.db.model.Columns;

import java.util.List;

public interface JobCategoryService {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    public JobCategory findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public JobCategory findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public JobCategory findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<JobCategory> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<JobCategory> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<JobCategory> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<JobCategory> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<JobCategory> findListByColumns(Columns columns, String orderBy, Integer count);


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
    public boolean delete(JobCategory model);


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
    public Object save(JobCategory model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(JobCategory model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(JobCategory model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<JobCategory> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<JobCategory> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<JobCategory> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);


    /**
    * 更新count数量
    *
    * @param categoryIds
    * @return boolean
    */
    public boolean updateCount(List<Long> categoryIds);

    /**
    * 分页查询  并添加父级信息
    *
    * @param columns
    * @param orderBy
    * @return com.jfinal.plugin.activerecord.Page<io.jpress.module.job.model.JobCategory>
    */
   List<JobCategory> findListByColumnsWithParent(Columns columns,String orderBy);
}