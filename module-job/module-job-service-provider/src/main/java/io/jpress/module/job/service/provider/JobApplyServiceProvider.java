package io.jpress.module.job.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobApply;
import io.jpress.module.job.service.JobApplyService;
import io.jpress.module.job.service.JobService;

import javax.validation.constraints.NotNull;

@Bean
public class JobApplyServiceProvider extends JPressServiceBase<JobApply> implements JobApplyService {


    @Inject
    private JobService jobService;

    /**
     * 分页查询并添加信息
     *
     * @param pagePara
     * @param pageSizePara
     * @param columns
     * @param orderBy
     * @return com.jfinal.plugin.activerecord.Page<io.jpress.module.job.model.JobApply>
     */
    @Override
    public Page<JobApply> paginateByColumnsWithInfo(int pagePara, int pageSizePara, Columns columns, String orderBy) {


        Page<JobApply> page = DAO.paginateByColumns(pagePara, pageSizePara, columns, orderBy);

        for (JobApply jobApply : page.getList()) {
             appendInfo(jobApply);
        }

        return page;
    }


    /**
     * 根据手机号 和 jobId 来确定是否已经申请
     *
     * @param jobId
     * @param mobile
     * @return io.jpress.module.job.model.JobApply
     */
    @Override
    public JobApply findFirstByJobIdWhitMobile(@NotNull Long jobId, @NotNull String mobile) {

        String sql = "select * from job_apply where job_id = ? and mobile = ?";

        return DAO.findFirst(sql,jobId,mobile);
    }


    public JobApply appendInfo(@NotNull JobApply jobApply){

        Job job = jobService.findById(jobApply.getJobId());
        if(job !=null){
            jobApply.put("job",job);
        }

        return jobApply;

    }



}