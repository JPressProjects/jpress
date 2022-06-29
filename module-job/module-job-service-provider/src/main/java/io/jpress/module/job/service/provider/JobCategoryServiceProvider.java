package io.jpress.module.job.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.sun.org.apache.bcel.internal.generic.Select;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jpress.model.User;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.model.JobCategory;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Bean
public class JobCategoryServiceProvider extends JbootServiceBase<JobCategory> implements JobCategoryService {

    @Inject
    private UserService userService;

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

    /**
     * 根据id查找信息 并添加对应信息
     *
     * @param entryId
     * @return io.jpress.module.job.model.JobCategory
     */
    @Override
    public JobCategory findByIdWithInfo(@NotNull long entryId) {

        JobCategory jobCategory = DAO.findById(entryId);

        if(jobCategory != null){
            User user = userService.findById(jobCategory.getUserId());
            if(user!=null){
                jobCategory.put("user",user);
            }
        }
        return jobCategory;
    }


}