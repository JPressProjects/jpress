package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.AopCache;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.JbootDb;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Role;
import io.jpress.model.SiteInfo;
import io.jpress.model.base.BaseRole;
import io.jpress.service.RoleService;
import io.jpress.service.SiteInfoService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 注意：此类不能继承 JPressServiceBase，否则其 site_id 字段将会被自动设置
 */
@Bean
public class SiteInfoServiceProvider extends JbootServiceBase<SiteInfo> implements SiteInfoService {


    @Inject
    private RoleService roleService;


    @Override
    @Cacheable(name = "site_info")
    public List<SiteInfo> findAll() {
        return super.findAll();
    }

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



    /**
     * 根据用户的 id 查询该用户可操作的站点列表
     * @param userId
     * @return
     */
    @Override
    public List<SiteInfo> findListByUserId(Long userId) {

        //是否是超级管理员
        boolean isSupperAdmin = roleService.isSupperAdmin(userId);
        if (isSupperAdmin){
            return findAll();
        }

        //用户的角色
        List<Role> userRoles = roleService.findListByUserId(userId);
        if (userRoles == null || userRoles.isEmpty()){
            return null;
        }

        long[] userRuleIds = userRoles.stream().mapToLong(BaseRole::getId).toArray();
        List<Record> records = JbootDb.find("site_role_mapping", Columns.create().in("role_id", userRuleIds));

        return records.stream().map(record -> findById(record.getLong("site_id")))
                .collect(Collectors.toList());
    }

    @Override
    public void shouldUpdateCache(int action, Model model, Object id) {
        AopCache.removeAll("site_info");
    }
}