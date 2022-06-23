package io.jpress.module.job.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.sun.org.apache.bcel.internal.generic.Select;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.model.JobCategory;
import io.jboot.service.JbootServiceBase;

import javax.validation.constraints.NotNull;
import java.util.List;

@Bean
public class JobCategoryServiceProvider extends JbootServiceBase<JobCategory> implements JobCategoryService {


    /**
     * 更新count数量
     *
     * @param categoryIds
     * @return boolean
     */
    @Override
    public boolean updateCount(@NotNull List<Long> categoryIds) {

        String sql = "select count(*) from job where category_id = ?";

        if(!categoryIds.isEmpty()){

            for (Long categoryId : categoryIds) {

                Long jobCount = Db.queryLong(sql, categoryId);

                JobCategory jobCategory = DAO.findById(categoryId);
                if(jobCategory != null){
                    jobCategory.setCount(jobCount);
                    jobCategory.update();
                }

            }

        }

        return true;
    }
}