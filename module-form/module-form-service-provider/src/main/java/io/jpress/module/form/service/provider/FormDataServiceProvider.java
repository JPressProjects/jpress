package io.jpress.module.form.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.module.form.service.FormDataService;

@Bean
public class FormDataServiceProvider implements FormDataService {

    @Override
    public Record findById(String tableName, Long dataId) {
        return Db.findById(tableName, dataId);
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
}