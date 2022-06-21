package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.SiteInfoService;
import io.jpress.model.SiteInfo;
import io.jboot.service.JbootServiceBase;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@Bean
public class SiteInfoServiceProvider extends JbootServiceBase<SiteInfo> implements SiteInfoService {



    /**
     * 储存 中间表信息
     *
     * @param siteId
     * @param roleIds
     * @return boolean
     */
    @Override
    public boolean saveOrUpdateSiteRoleMapping(@NotNull Long siteId,Long[] roleIds) {

        //查询数据库相记录
        String findSql = "select role_id from site_role_mapping where site_id = ?";
        List<Record> records = Db.find(findSql, siteId);

        //如果有记录  才删除所有
        if(records != null) {
            String sql = "delete from site_role_mapping where site_id = ?";
            Db.delete(sql, siteId);
        }

        for (Long roleId : roleIds) {
            Record record = new Record();
            record.set("site_id",siteId);
            record.set("role_id",roleId);
            Db.save("site_role_mapping", record);
        }


        return true;
    }
}