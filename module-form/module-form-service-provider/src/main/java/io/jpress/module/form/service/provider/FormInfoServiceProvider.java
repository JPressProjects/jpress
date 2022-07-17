package io.jpress.module.form.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.form.model.FieldInfo;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

import java.util.List;

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

        Db.update(formInfo.toCreateTableSql());

        //表单的版本大于1，发布多次了
        if (formInfo.getVersion() > 1) {

            //copy 数据
            copyOldData(formInfo);

            //删除旧的表
            deleteOldTable(formInfo);

        }
    }


    private void copyOldData(FormInfo formInfo) {
        List<FieldInfo> fieldInfos = formInfo.getFieldInfos();

        Db.each(record -> {
            Record newRecord = new Record();
            newRecord.put("id", record.get("id"));

            fieldInfos.forEach(fieldInfo -> {
                Object oldValue = record.get(fieldInfo.getFieldName());
                newRecord.put(fieldInfo.getFieldName(), fieldInfo.convertValueData(oldValue));
            });

            Db.save(formInfo.getCurrentTableName(), newRecord);
            return true;
        }, "select * from " + formInfo.getPrevTableName());
    }


    private void deleteOldTable(FormInfo formInfo) {
        Db.update("DROP TABLE " + formInfo.getPrevTableName());
    }
}