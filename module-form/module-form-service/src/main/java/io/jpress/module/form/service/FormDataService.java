package io.jpress.module.form.service;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.db.model.Columns;

public interface FormDataService {


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


}