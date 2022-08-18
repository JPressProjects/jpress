package io.jpress.module.form.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.form.model.FormInfo;
import io.jboot.db.model.Columns;

import java.util.List;

public interface FormInfoService {


    /**
     * 发布表单
     *
     * @param formInfo
     */
    void publish(FormInfo formInfo);

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    public FormInfo findById(Object id);


    /**
     * 根据uuid 查询model
     *
     * @param uuid
     * @return
     */
    public FormInfo findByUUID(String uuid);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public FormInfo findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public FormInfo findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<FormInfo> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<FormInfo> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<FormInfo> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<FormInfo> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<FormInfo> findListByColumns(Columns columns, String orderBy, Integer count);


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
    public boolean delete(FormInfo model);


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
    public Object save(FormInfo model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(FormInfo model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(FormInfo model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<FormInfo> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<FormInfo> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<FormInfo> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);

    /**分页查询 并添加信息
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return com.jfinal.plugin.activerecord.Page<io.jpress.module.form.model.FormInfo>
     */
    public Page<FormInfo> paginateByColumnsWithInfo(int page, int pageSize, Columns columns, String orderBy);

}