package io.jpress.module.form.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.form.model.FormDictItem;
import io.jboot.db.model.Columns;

import java.util.List;

public interface FormDictItemService  {

    /**
     * 根据主键查找Model
     *
     * @param id
     * @return
     */
    public FormDictItem findById(Object id);


    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @return
     */
    public FormDictItem findFirstByColumns(Columns columns);

    /**
     * 根据 Columns 查找单条数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public FormDictItem findFirstByColumns(Columns columns, String orderBy);


    /**
     * 查找全部数据
     *
     * @return
     */
    public List<FormDictItem> findAll();


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @return
     */
    public List<FormDictItem> findListByColumns(Columns columns);


    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @return
     */
    public List<FormDictItem> findListByColumns(Columns columns, String orderBy);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param count
     * @return
     */
    public List<FormDictItem> findListByColumns(Columns columns, Integer count);

    /**
     * 根据 Columns 查找数据
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return
     */
    public List<FormDictItem> findListByColumns(Columns columns, String orderBy, Integer count);


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
    public boolean delete(FormDictItem model);


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
    public Object save(FormDictItem model);


    /**
     * 保存或更新
     *
     * @param model
     * @return id if success
     */
    public Object saveOrUpdate(FormDictItem model);

    /**
     * 更新
     *
     * @param model
     * @return
     */
    public boolean update(FormDictItem model);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<FormDictItem> paginate(int page, int pageSize);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<FormDictItem> paginateByColumns(int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<FormDictItem> paginateByColumns(int page, int pageSize, Columns columns, String orderBy);
    
    /**
    * 根据 dict id 删除数据
    *
    * @param dictId
    * @return boolean
    */
    public boolean deleteByDictId(Long dictId);


}