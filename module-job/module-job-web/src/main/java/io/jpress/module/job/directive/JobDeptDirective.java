package io.jpress.module.job.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.JobDepartment;
import io.jpress.module.job.service.JobDepartmentService;
import java.util.List;

/**
 * @version V5.0
 * @description: 所有岗位部门
 */
@JFinalDirective("jobDepartments")
public class JobDeptDirective extends JbootDirectiveBase {


    @Inject
    private JobDepartmentService jobDepartmentService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        List<JobDepartment> departmentList = jobDepartmentService.findAll();

        if (departmentList == null) {
            return;
        }

        scope.setLocal("departmentList", departmentList);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
