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
 * @version V5.0
 * @Title: 最新岗位
 */
@JFinalDirective("newJobs")
public class newJobs extends JbootDirectiveBase {

    @Inject
    private JobService jobService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Integer count = getParaToInt(0, scope);

        List<Job> jobList;

        if(count == null || count <= 0){
            jobList = jobService.findListByColumns(Columns.create(), "created desc");
        }else {
            jobList = jobService.findListByColumns(Columns.create(),"created desc",count);
        }

        scope.setLocal("newJobList",jobList);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
