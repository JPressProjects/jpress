package io.jpress.module.job.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
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


    /**
     * 分页查询  并添加父级信息
     *
     * @param columns
     * @return com.jfinal.plugin.activerecord.Page<io.jpress.module.job.model.JobCategory>
     */
    @Override
    public List<JobCategory> findListByColumnsWithParent(Columns columns,String orderBy) {

        List<JobCategory> categoryList = DAO.findListByColumns(columns,orderBy);

        for (JobCategory jobCategory : categoryList) {

            appendInfo(jobCategory);

        }

        return categoryList;
    }


    /**
    * 追加 category 对应的信息
    *
    * @return io.jpress.module.job.model.JobCategory
    */
    private JobCategory appendInfo(@NotNull JobCategory jobCategory){

        //添加父级 信息
        if(jobCategory.getPid() != null && jobCategory.getPid() != 0){
            JobCategory jobParent = DAO.findById(jobCategory.getPid());
            jobCategory.setParent(jobParent);
        }


        return jobCategory;
    }

}