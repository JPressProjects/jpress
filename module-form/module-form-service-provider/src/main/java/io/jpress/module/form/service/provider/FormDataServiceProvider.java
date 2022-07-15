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
    public Object save(String tableName, Record record) {
        return Db.save(tableName, record);
    }


    @Override
    public Page<Record> paginate(String tableName, int page, int pageSize) {
        return Db.paginate(page, pageSize, "select * ", "from " + tableName + " order by id desc");
    }

    @Override
    public Page<Record> paginateByColumns(String tableName, int page, int pageSize, Columns columns) {
        String wherePartSql = columns.toWherePartSql();
        return Db.paginate(page, pageSize, "select * ", "from " + tableName + " where " + wherePartSql);
    }

    @Override
    public Page<Record> paginateByColumns(String tableName, int page, int pageSize, Columns columns, String orderBy) {
        String wherePartSql = columns.toWherePartSql();
        return Db.paginate(page, pageSize, "select * ", "from " + tableName + " where " + wherePartSql + " order by " + orderBy);
    }
}