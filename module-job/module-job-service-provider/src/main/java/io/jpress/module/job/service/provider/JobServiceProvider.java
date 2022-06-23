package io.jpress.module.job.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.rpc.annotation.RPCInject;
import io.jboot.db.model.Columns;
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.model.JobDepartment;
import io.jpress.module.job.service.JobAddressService;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.module.job.service.JobDepartmentService;
import io.jpress.module.job.service.JobService;
import io.jpress.module.job.model.Job;
import io.jboot.service.JbootServiceBase;

import javax.validation.constraints.NotNull;

@Bean
public class JobServiceProvider extends JbootServiceBase<Job> implements JobService {


    @Inject
    private JobCategoryService jobCategoryService;

    @Inject
    private JobDepartmentService jobDepartmentService;

    @Inject
    private JobAddressService jobAddressService;


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
    * 添加各种信息 分类 地址 部门等....
    *
    * @param job
    * @return io.jpress.module.job.model.Job
    */
    public Job addInfo(@NotNull Job job){

        JobCategory jobCategory = jobCategoryService.findById(job.getCategoryId());
        if(jobCategory!=null){
            job.put("category",jobCategory);
        }

        JobDepartment jobDepartment = jobDepartmentService.findById(job.getDeptId());
        if(jobDepartment!=null){
            job.put("dept",jobDepartment);
        }

        JobAddress jobAddress = jobAddressService.findById(job.getAddressId());
        if(jobAddress!=null){
            job.put("address",jobAddress);
        }

        return job;
    }
}