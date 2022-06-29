package io.jpress.module.job.controller.front;

import com.jfinal.aop.Inject;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.TemplateControllerBase;

import java.util.List;


/**
 * @version V5.0
 * @Title: Job 的 Controller，主要是用过 {{@link io.jpress.module.job.directive.JobPageDirective 来渲染的}}
 */
@RequestMapping("/job/list")
public class JobListController extends TemplateControllerBase {

    @Inject
    private JobService jobService;

    public void index() {
        Long categoryId = getParaToLong("categoryId");
        Long deptId = getParaToLong("deptId");
        Long addressId = getParaToLong("addressId");
        Integer education = getParaToInt("education");
        Integer workYear = getParaToInt("workYear");
        Integer workType = getParaToInt("workType");
        Integer recruitmentType = getParaToInt("recruitmentType");

        int page = getParaToInt("page", 1);

        if(page > 0){
            setAttr("__pageNumber",page);
        }

        Columns columns = new Columns();
        columns.eq("categoryId",categoryId);
        columns.eq("deptId",deptId);
        columns.eq("addressId",addressId);
        columns.eq("education",education);
        columns.eq("workYear",workYear);
        columns.eq("workType",workType);
        columns.eq("recruitmentType",recruitmentType);

        List<Column> jobColumnsList = columns.getList();
        setAttr("jobColumnsList",jobColumnsList);

        render("joblist.html");
    }




}
