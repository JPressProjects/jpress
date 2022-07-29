package io.jpress.module.form.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

@Bean
public class FormInfoServiceProvider extends JPressServiceBase<FormInfo> implements FormInfoService {

    /**
     * 发布表单
     * 1、创建新的表
     * 2、copy 旧表的数据到新表
     * 3、删除 旧表
     *
     * @param formInfo
     */
    @Override
    public void publish(FormInfo formInfo) {

        String createTableSql = formInfo.toCreateTableSql();
        int update = Db.update(createTableSql);

        //第一次发布
        if (formInfo.getVersion() == 1) {
            return;
        }

        //copy 数据
        copyOldData(formInfo);

        //删除旧的表
        deleteOldTable(formInfo);
    }


    private void copyOldData(FormInfo formInfo) {
        Db.each(record -> {
            Record newRecord = formInfo.newRecord(record);
            Db.save(formInfo.getCurrentTableName(), newRecord);
            return true;
        }, "select * from " + formInfo.getPrevTableName());
    }


    private void deleteOldTable(FormInfo formInfo) {
        Db.update("DROP TABLE " + formInfo.getPrevTableName());
    }
}