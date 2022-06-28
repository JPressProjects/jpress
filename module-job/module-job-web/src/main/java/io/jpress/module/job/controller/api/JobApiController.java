package io.jpress.module.job.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.apidoc.annotation.ApiResp;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.ApiControllerBase;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @version V5.0
 * @Title: 岗位相关的 API
 */
@RequestMapping("/api/job")
@Api("招聘相关API文档")
public class JobApiController extends ApiControllerBase {

    @Inject
    private JobService jobService;


    @ApiOper(value = "岗位详情", paraNotes = "ID必须传入一个值")
    @ApiResp(field = "jobDetail", dataType = Job.class, notes = "岗位详情")
    public Ret detail(@ApiPara("岗位ID") Long id) {

        if (id == null) {
            return Ret.fail().set("message", "id必须有一个值");
        }

        Job job = jobService.findByIdWithInfo(id);

        if (job == null) {
            return Ret.fail().set("message", "该岗位不存在");
        }

        return Ret.ok().set("jobDetail", job);
    }


    @ApiOper("岗位分页读取")
    @ApiResp(field = "page", notes = "岗位分页数据", dataType = Page.class, genericTypes = Job.class)
    public Ret paginate(@ApiPara("排序方式") String orderBy,
                        @ApiPara("岗位需要学历") Integer education,
                        @ApiPara("岗位分类ID") Long categoryId,
                        @ApiPara("岗位部门ID") Long deptId,
                        @ApiPara("岗位地区ID") Long addressId,
                        @ApiPara("岗位工作年限") Integer workYear,
                        @ApiPara("岗位工作类型") Integer workType,
                        @ApiPara("岗位招聘类型") Integer recruitmentType,
                        @ApiPara("分页的页码") @DefaultValue("1") int pageNumber,
                        @ApiPara("每页的数据数量") @DefaultValue("10") int pageSize) {

        Columns columns = new Columns();
        columns.eq("education", education);
        columns.eq("category_id", categoryId);
        columns.eq("dept_id", deptId);
        columns.eq("address_id", addressId);
        columns.eq("work_year", workYear);
        columns.eq("recruitment_type", recruitmentType);
        columns.eq("work_type", workType);

        Page<Job> page = jobService.paginateByColumnsWithInfo(pageNumber, pageSize, columns, orderBy);

        return Ret.ok().set("page", page);

    }


    @ApiOper("根据自定义条件查找岗位列表")
    @ApiResp(field = "list", notes = "岗位列表", dataType = List.class, genericTypes = Job.class)
    public Ret listByColumns(@ApiPara("排序方式") String orderBy,
                             @ApiPara("岗位需要学历") Integer education,
                             @ApiPara("岗位分类ID") Long categoryId,
                             @ApiPara("岗位部门ID") Long deptId,
                             @ApiPara("岗位地区ID") Long addressId,
                             @ApiPara("岗位工作年限") Integer workYear,
                             @ApiPara("岗位工作类型") Integer workType,
                             @ApiPara("岗位招聘类型") Integer recruitmentType,
                             @ApiPara("需要查询的数量") Integer count) {

        Columns columns = new Columns();
        columns.eq("category_id", categoryId);
        columns.eq("address_id", addressId);
        columns.eq("dept_id", deptId);
        columns.eq("education", education);
        columns.eq("work_year", workYear);
        columns.eq("work_type", workType);
        columns.eq("recruitment_type", recruitmentType);

        List<Job> jobList = jobService.findListByColumnsWithInfo(columns, orderBy, count);
        return Ret.ok().set("list", jobList);
    }


    @ApiOper("删除岗位")
    public Ret doDelete(@ApiPara("岗位ID") @NotNull Long id) {
        jobService.deleteById(id);
        return Rets.OK;
    }

    @ApiOper(value = "创建新岗位", contentType = ContentType.JSON)
    @ApiResp(field = "id", notes = "岗位ID", dataType = Long.class, mock = "123")
    public Ret doCreate(@ApiPara("岗位的 JSON 信息") @JsonBody Job job) {
        Object id = jobService.save(job);
        return Ret.ok().set("id", id);
    }

    @ApiOper(value = "更新岗位", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("岗位的 JSON 信息") @JsonBody Job job) {
        jobService.update(job);
        return Rets.OK;
    }


}
