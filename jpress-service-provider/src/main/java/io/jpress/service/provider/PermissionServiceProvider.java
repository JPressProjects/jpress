package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.PermissionService;
import io.jpress.model.Permission;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class PermissionServiceProvider extends JbootServiceBase<Permission> implements PermissionService {

    @Override
    public int sync(List<Permission> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return 0;
        }

        int syncCounter = 0;
        for (Permission permission : permissions) {

            Columns columns = Columns.create("controller", permission.getController());
            columns.eq("actionKey", permission.getActionKey());

            Permission dbPermission = DAO.findFirstByColumns(columns);
            
            if (dbPermission == null) {
                permission.save();
                syncCounter++;
            }
        }
        return syncCounter;
    }
}