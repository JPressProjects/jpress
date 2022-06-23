package io.jpress.module.job.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobDepartmentService;
import io.jpress.module.job.model.JobDepartment;
import io.jboot.service.JbootServiceBase;

import javax.validation.constraints.NotNull;
import java.util.List;

@Bean
public class JobDepartmentServiceProvider extends JbootServiceBase<JobDepartment> implements JobDepartmentService {


    @RPCInject
    private JobDepartmentService jobDepartmentService;


    /**
     * 更新count数量
     *
     * @param deptIds
     */
    @Override
    public boolean updateCount(@NotNull List<Long> deptIds) {

        String sql = "select count(*) from job where dept_id = ?";

        if(!deptIds.isEmpty()){

            for (Long deptId : deptIds) {

                Long deptCount = Db.queryLong(sql, deptId);

                JobDepartment department = DAO.findById(deptId);
                if(department != null){
                    department.setCount(deptCount);
                    department.update();
                }

            }

        }

        return true;
    }
}