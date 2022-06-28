package io.jpress.module.job.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
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
import io.jpress.module.job.model.JobDepartment;
import io.jpress.module.job.service.JobDepartmentService;
import io.jpress.web.base.ApiControllerBase;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @version V5.0
 * @Title: 岗位部门相关的 API
 */
@RequestMapping("/api/job/dept")
@Api("岗位部门相关API文档")
public class JobDeptApiController extends ApiControllerBase {

    @Inject
    private JobDepartmentService jobDepartmentService;

    @ApiOper(value = "岗位部门详情", paraNotes = "id不能为空")
    @ApiResp(field = "detail", dataType = JobDepartment.class, notes = "岗位部门详情")
    public Ret detail(@ApiPara("部门ID") Long id) {


        if (id == null) {
            return Ret.fail().set("message", "id不能为空");
        }

        JobDepartment jobDepartment = jobDepartmentService.findById(id);

        if (jobDepartment == null) {
            return Ret.fail().set("message", "当前分类不存在");
        }

        return Ret.ok("detail", jobDepartment);

    }


    @ApiOper("根据自定义条件查找岗位部门列表")
    @ApiResp(field = "list", notes = "岗位部门列表", dataType = List.class, genericTypes = JobDepartment.class)
    public Ret listByColumns(@ApiPara("排序属性") @DefaultValue("order_number asc") String orderBy,
                             @ApiPara("查询数量") Integer count) {

        List<JobDepartment> jobDepartmentList;

        if (count == null || count <= 0) {
            jobDepartmentList = jobDepartmentService.findListByColumns(Columns.create(), orderBy);
        } else {
            jobDepartmentList = jobDepartmentService.findListByColumns(Columns.create(), orderBy, count);
        }

        return Ret.ok().set("list", jobDepartmentList);
    }



    @ApiOper("删除岗位部门")
    public Ret doDelete(@ApiPara("部门ID") @NotNull Long id) {
        jobDepartmentService.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "创建新的岗位部门", contentType = ContentType.JSON)
    @ApiResp(field = "id", notes = "岗位部门ID", dataType = Long.class, mock = "123")
    public Ret doCreate(@ApiPara("岗位部门json") @JsonBody JobDepartment jobDepartment) {
        Object id = jobDepartmentService.save(jobDepartment);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "更新岗位部门", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("岗位部门json") @JsonBody JobDepartment jobDepartment) {
        jobDepartmentService.update(jobDepartment);
        return Rets.OK;
    }
}
