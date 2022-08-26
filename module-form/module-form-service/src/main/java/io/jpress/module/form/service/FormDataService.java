package io.jpress.module.form.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.model.Columns;

import java.util.List;

public interface FormDataService {


    /**
     * 根据 ID 查找
     *
     * @param tableName
     * @param dataId
     * @return
     */
    Record findById(String tableName, Long dataId);


    /**
     * 根据 id 删除
     *
     * @param tableName
     * @param dataId
     */
    void deleteById(String tableName, Long dataId);

    /**
     * 保存到数据库
     *
     * @param tableName
     * @param record
     * @return id if success
     */
    public Object save(String tableName, Record record);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Record> paginate(String tableName, int page, int pageSize);


    /**
     * 分页
     *
     * @param tableName
     * @param page
     * @param pageSize
     * @param orderBy
     * @return
     */
    public Page<Record> paginate(String tableName, int page, int pageSize, String orderBy);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Record> paginateByColumns(String tableName, int page, int pageSize, Columns columns);


    /**
     * 分页
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return
     */
    public Page<Record> paginateByColumns(String tableName, int page, int pageSize, Columns columns, String orderBy);

    /**
    * 查询所有
    *
    * @param tableName
    * @return java.util.List<com.jfinal.plugin.activerecord.Record>
    */
    public List<Record> findAll(String tableName);
    
    /**
    * 根据 field 数据查询数据
    *
    * @param tableName
    * @param filedName
    * @param value
    * @return int
    */
    public Long findCountByValue(String tableName,String filedName,String value);


    public Integer findCountByTable(String tableName);

    /**
    * 删除数据表
    *
    * @param tableName
    * @return boolean
    */
    public void deleteTable(String tableName);

}