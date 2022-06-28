package io.jpress.module.job.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.service.JobService;

import java.util.List;


/**
 * @description: 根据条件查询岗位列表
 * @version: v5.0
 **/
@JFinalDirective("jobs")
public class JobsDirective extends JbootDirectiveBase {

    @Inject
    private JobService jobService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        //可以指定分类ID
        Long categoryId = getParaToLong("categoryId", scope);
        //可以指定部门ID
        Long deptId = getParaToLong("deptId", scope);
        //可以指定部门ID
        Long addressId = getParaToLong("addressId", scope);
        //可以指定查询数量
        Integer count = getParaToInt("count", scope);
        //可以指定学历
        Integer record = getParaToInt("record", scope);
        //可以指定年限
        Integer year = getParaToInt("year", scope);
        //可以指定工作类型
        Integer jobType = getParaToInt("jobType", scope);
        //可以指定工作类型
        Integer recruitType = getParaToInt("recruitType", scope);
        //指定排序属性
        String orderBy = getPara("orderBy", scope, "created desc");

        Columns columns = new Columns();
        columns.eq("category_id", categoryId);
        columns.eq("dept_id", deptId);
        columns.eq("address_id", addressId);
        columns.eq("record", record);
        columns.eq("year", year);
        columns.eq("job_type", jobType);
        columns.eq("recruitment_type", recruitType);

        List<Job> jobList = jobService.findListByColumnsWithInfo(columns, orderBy, count);

        if (jobList == null || jobList.isEmpty()) {
            return;
        }

        scope.setLocal("jobList", jobList);
        renderBody(env, scope, writer);

    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
