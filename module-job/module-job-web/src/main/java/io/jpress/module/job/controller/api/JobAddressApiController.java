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
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobAddressService;
import io.jpress.web.base.ApiControllerBase;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;

/**
 * @version V5.0
 * @Title: 岗位地区相关的 API
 */
@RequestMapping("/api/job/address")
@Api("岗位地区相关API文档")
public class JobAddressApiController extends ApiControllerBase {

    @Inject
    private JobAddressService jobAddressService;


    @ApiOper(value = "岗位地区详情", paraNotes = "id不能为空")
    @ApiResp(field = "detail", dataType = JobCategory.class, notes = "岗位地区详情")
    public Ret detail(@ApiPara("地区ID") Long id){

        if (id == null ) {
            return Ret.fail().set("message", "id不能为空");
        }

        JobAddress jobAddress = jobAddressService.findById(id);

        if(jobAddress == null){
            return Ret.fail().set("message", "当前分类不存在");
        }


        return Ret.ok("detail", jobAddress);

    }


    @ApiOper("根据自定义条件查找岗位地区列表")
    @ApiResp(field = "list", notes = "岗位地区列表", dataType = List.class, genericTypes = JobAddress.class)
    public Ret listByColumns(@ApiPara("排序属性") @DefaultValue("order_number asc") String orderBy){

        List<JobAddress> jobAddressList = jobAddressService.findListByColumns(Columns.create(), orderBy);
        if (jobAddressList == null || jobAddressList.isEmpty()) {
            return Ret.ok().set("list", new HashMap<>());
        }

        return Ret.ok().set("list", jobAddressList);
    }



    @ApiOper("删除岗位地区")
    public Ret doDelete(@ApiPara("地区ID") @NotNull Long id) {
        jobAddressService.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "创建新的岗位地区",contentType = ContentType.JSON)
    @ApiResp(field = "id", notes = "岗位地区", dataType = Long.class, mock = "123")
    public Ret doCreate(@ApiPara("岗位地区json") @JsonBody JobAddress jobAddress) {
        Object id = jobAddressService.save(jobAddress);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "更新岗位地区",contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("岗位地区json") @JsonBody JobAddress jobAddress) {
        jobAddressService.update(jobAddress);
        return Rets.OK;
    }
}