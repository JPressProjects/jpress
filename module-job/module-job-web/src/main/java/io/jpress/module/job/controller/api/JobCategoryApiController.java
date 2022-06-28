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
import io.jpress.commons.layer.SortKit;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobCategoryService;
import io.jpress.web.base.ApiControllerBase;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version V5.0
 * @Title: 岗位分类相关的 API
 */
@RequestMapping("/api/job/category")
@Api("岗位分类相关API文档")
public class JobCategoryApiController extends ApiControllerBase {


    @Inject
    private JobCategoryService jobCategoryService;


    @ApiOper(value = "岗位分类详情", paraNotes = "id不能为空")
    @ApiResp(field = "detail", dataType = JobCategory.class, notes = "岗位分类详情")
    public Ret detail(@ApiPara("岗位分类ID") Long id){

        if (id == null ) {
            return Ret.fail().set("message", "id 不能为空");
        }

        JobCategory category = jobCategoryService.findById(id);

        if(category == null){
            return Ret.fail().set("message", "当前分类不存在");
        }


        return Ret.ok("detail", category);

    }


    @ApiOper("根据自定义条件查找岗位分类列表")
    @ApiResp(field = "list", notes = "岗位分类列表", dataType = List.class, genericTypes = JobCategory.class)
    public Ret listByColumns(@ApiPara("分类父ID") Long pid,
                             @ApiPara("分类创建用户ID") Long userId,
                             @ApiPara("排序属性") @DefaultValue("order_number asc") String orderBy){

        List<JobCategory> categories = jobCategoryService.findListByColumns(Columns.create().eq("user_id",userId),orderBy);
        if (categories == null || categories.isEmpty()) {
            return Ret.ok().set("list", new HashMap<>());
        }

        if (pid != null) {
            categories = categories.stream()
                    .filter(category -> pid.equals(category.getPid()))
                    .collect(Collectors.toList());
        } else {
            SortKit.toTree(categories);
        }

        return Ret.ok().set("list", categories);
    }



    @ApiOper("删除岗位分类")
    public Ret doDelete(@ApiPara("岗位分类ID") @NotNull Long id) {
        jobCategoryService.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "创建新的岗位分类",contentType = ContentType.JSON)
    @ApiResp(field = "id", notes = "岗位分类ID", dataType = Long.class, mock = "123")
    public Ret doCreate(@ApiPara("岗位分类json") @JsonBody JobCategory jobCategory) {
        Object id = jobCategoryService.save(jobCategory);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "更新岗位分类",contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("岗位分类json") @JsonBody JobCategory jobCategory) {
        jobCategoryService.update(jobCategory);
        return Rets.OK;
    }


}
