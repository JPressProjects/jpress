package io.jpress.module.form.service.provider;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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


    /**
     * 根据uuid 查询model
     *
     * @param uuid
     * @return
     */
    @Override
    public FormInfo findByUUID(@NotNull String uuid) {

        String sql = "select * from form_info where uuid = ?";

        return DAO.findFirst(sql, uuid);
    }

    /**
     * 分页查询 并添加信息
     *
     * @param page
     * @param pageSize
     * @param columns
     * @param orderBy
     * @return com.jfinal.plugin.activerecord.Page<io.jpress.module.form.model.FormInfo>
     */
    @Override
    public Page<FormInfo> paginateByColumnsWithInfo(int page, int pageSize, Columns columns, String orderBy) {

        Page<FormInfo> pageList = DAO.paginateByColumns(page, pageSize, columns, orderBy);

        for (FormInfo formInfo : pageList.getList()) {
            appendInfo(formInfo);
        }

        return pageList;
    }


    private void copyOldData(FormInfo formInfo) {
        try {
            Db.each(record -> {
                Record newRecord = formInfo.newRecord(record);
                Db.save(formInfo.getCurrentTableName(), newRecord);
                return true;
            }, "select * from " + formInfo.getPrevTableName());
        } catch (Exception e) {
            LogKit.error("Copy form data error. {}", e.getMessage());
        }

    }


    private void deleteOldTable(FormInfo formInfo) {
        try {
            Db.update("DROP TABLE " + formInfo.getPrevTableName());
        } catch (Exception e) {
            LogKit.error("Delete form table error. {}", e.getMessage());
        }
    }


    // formInfo 添加数据
    private FormInfo appendInfo(@NotNull FormInfo formInfo) {

        if (formInfo.getDataCreated() != null) {

            String timeDifference = getTimeDifference(formInfo.getDataCreated(), new Date());

            formInfo.put("timeDifference", timeDifference);

        }

        return formInfo;

    }


    //计算时间差  只到分钟
    private String getTimeDifference(Date date1, Date date2) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();

        try {

            bef.setTime(dateFormat.parse(dateFormat.format(date1)));
            aft.setTime(dateFormat.parse(dateFormat.format(date2)));


            int year = aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR);
            int mouths = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
            int dates = aft.get(Calendar.DATE) - bef.get(Calendar.DATE);
            int hours = aft.get(Calendar.HOUR) - bef.get(Calendar.HOUR);
            int minutes = aft.get(Calendar.MINUTE) - bef.get(Calendar.MINUTE);

            if (year > 0) {

                return year + "年";

            } else if (mouths > 0) {

                return mouths + "个月";

            } else if (dates > 0) {

                return dates + "天";

            } else if (hours > 0) {

                return hours + "小时";

            } else if (minutes > 0) {

                return minutes + "分钟";

            } else {

                return "0分钟";
            }


        } catch (Exception e) {
            e.printStackTrace();
            LogKit.error(e.getMessage());
        }

        return null;

    }

}