package io.jpress.module.job.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.User;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Bean
public class JobCategoryServiceProvider extends JPressServiceBase<JobCategory> implements JobCategoryService {

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

}