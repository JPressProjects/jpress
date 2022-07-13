package io.jpress.module.job.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.service.JobService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Bean
public class JobServiceProvider extends JPressServiceBase<Job> implements JobService {


    @Inject
    private JobCategoryService jobCategoryService;



    /**
     * 分页查询并且添加信息
     *
     * @param pagePara
     * @param pageSizePara
     * @return com.jfinal.plugin.activerecord.Page<io.jpress.module.job.model.Job>
     */
    @Override
    public Page<Job> paginateByColumnsWithInfo(int pagePara, int pageSizePara, Columns columns, String orderBy) {

        Page<Job> page = DAO.paginateByColumns(pagePara, pageSizePara, columns, orderBy);

        for (Job job : page.getList()) {
            addInfo(job);
        }

        return page;
    }

    /**
     * 根据id 查询 job 对象 并添加信息
     *
     * @param id
     * @return io.jpress.module.job.model.Job
     */
    @Override
    public Job findByIdWithInfo(@NotNull Long id) {

        Job job = DAO.findById(id);

        if(job == null){ return null; }

        return addInfo(job);
    }

    /**
     * 根据各种条件查询 job
     *
     * @param columns
     * @param orderBy
     * @return java.util.List<io.jpress.module.job.model.Job>
     */
    @Override
    public List<Job> findListByColumnsWithInfo(Columns columns, String orderBy, Integer count) {

        List<Job> jobList;

        if(count == null || count <= 0){
            jobList = DAO.findListByColumns(columns, orderBy);
        }else {
            jobList = DAO.findListByColumns(columns, orderBy , count);
        }

        for (Job job : jobList) {
            addInfo(job);
        }

        return jobList.isEmpty() ? null : jobList;
    }


    /**
    * 添加各种信息 分类 地址 等....
    *
    * @param job
    * @return io.jpress.module.job.model.Job
    */
    public Job addInfo(@NotNull Job job){

        JobCategory jobCategory = jobCategoryService.findById(job.getCategoryId());
        if(jobCategory!=null){
            job.put("category",jobCategory);
        }

        return job;
    }
}