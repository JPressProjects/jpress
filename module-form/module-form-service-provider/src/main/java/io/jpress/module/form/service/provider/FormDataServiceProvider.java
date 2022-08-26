package io.jpress.module.form.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.form.service.FormDataService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Bean
public class FormDataServiceProvider implements FormDataService {


    @Override
    public Record findById(String tableName, Long dataId) {
        if (tableName == null || dataId == null) {
            return null;
        }

        return Db.findById(tableName, dataId);
    }

    @Override
    public void deleteById(String tableName, Long dataId) {
        if (tableName == null || dataId == null) {
            return;
        }

        Db.deleteById(tableName, dataId);
    }

    @Override
    public Object save(String tableName, Record record) {
        return Db.save(tableName, record);
    }


    @Override
    public Page<Record> paginate(String tableName, int page, int pageSize) {
        return paginate(tableName, page, pageSize, "id desc");
    }


    @Override
    public Page<Record> paginate(String tableName, int page, int pageSize, String orderBy) {
        return Db.paginate(page, pageSize, "select * ", "from " + tableName + " order by " + orderBy);
    }

    @Override
    public Page<Record> paginateByColumns(String tableName, int page, int pageSize, Columns columns) {
        return paginateByColumns(tableName, page, pageSize, columns, "id desc");
    }

    @Override
    public Page<Record> paginateByColumns(String tableName, int page, int pageSize, Columns columns, String orderBy) {
        if (columns == null || columns.isEmpty()) {
            return paginate(tableName, page, pageSize, orderBy);
        } else {
            String wherePartSql = columns.toWherePartSql();
            return Db.paginate(page, pageSize, "select * "
                    , "from " + tableName + " where " + wherePartSql + " order by " + orderBy
                    , columns.getValueArray());
        }
    }

    /**
     * 查询所有
     *
     * @param tableName
     * @return java.util.List<com.jfinal.plugin.activerecord.Record>
     */
    @Override
    public List<Record> findAll(String tableName) {
        return Db.findAll(tableName);
    }


    /**
     * 根据 field 查询数据量
     *
     * @param tableName
     * @param fieldName
     * @param value
     * @return int
     */
    @Override
    public Long findCountByValue(String tableName, String fieldName, String value) {
        if (SqlUtils.hasSqlInject(tableName) || SqlUtils.hasSqlInject(fieldName) || SqlUtils.hasSqlInject(value)){
            return 0L;
        }

        String sql = "select count(*) from " + tableName + " where `" + fieldName + "` = ?";
        return Db.queryLong(sql,value);
    }



    @Override
    public Integer findCountByTable(String tableName) {
        if (SqlUtils.hasSqlInject(tableName)){
            return 0;
        }

        String sql = "select count(*)  from " + tableName;
        return Db.queryInt(sql);
    }

    /**
     * 删除数据表
     *
     * @param tableName
     * @return boolean
     */
    @Override
    public void deleteTable(@NotNull String tableName) {

        String sql = "DROP TABLE IF EXISTS "+tableName;

        Db.delete(sql);

    }


}