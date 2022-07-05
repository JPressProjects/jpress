package io.jpress.module.job.directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.JbootPaginateDirective;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.model.JobDepartment;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.TemplateControllerBase;


/**
 * @description: 分页查询 job
 * @version: v5.0
 **/
@JFinalDirective("jobPage")
public class JobPageDirective extends JbootDirectiveBase {

    @Inject
    private JobService jobService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        TemplateControllerBase controller = (TemplateControllerBase) JbootControllerContext.get();

        Columns columns = new Columns();
        columns.eq("category_id",controller.getPara("categoryId"));
        columns.eq("dept_id", controller.getPara("deptId"));
        columns.eq("address_id", controller.getPara("addressId"));
        columns.eq("education", controller.getPara("education"));
        columns.eq("work_year", controller.getPara("workYear"));
        columns.eq("recruitment_type", controller.getPara("recruitmentType"));
        columns.eq("work_type", controller.getPara("workType"));
        columns.likeAppendPercent("title",controller.getPara("title"));

        //分页查询并添加信息
        Integer pageSize = getParaToInt("pageSize", scope, 10);
        String  orderBy = getParaToString("orderBy", scope, "id desc");
        Page<Job> jobPage = jobService.paginateByColumnsWithInfo(controller.getParaToInt("page",1), pageSize, columns, orderBy);


        scope.setGlobal("jobPage", jobPage);


        renderBody(env, scope, writer);
    }




    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("jobPaginate")
    public static class TemplatePaginateDirective extends JbootPaginateDirective {

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("jobPage");
        }
    }

}
