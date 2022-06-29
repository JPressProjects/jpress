package io.jpress.module.job.directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.service.JobService;

/**
 * @description: select job by id
 * @version: v5.0
 **/
@JFinalDirective("job")
public class JobDirective extends JbootDirectiveBase {

    @Inject
    private JobService jobService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Long id = getParaToLong(0, scope);

        Job job;

        Job controllerJob = JbootControllerContext.get().getAttr("job");

        //如果俩个都为空
        if (controllerJob == null && (id == null || id <= 0)) {
            return;
        }

        //如果 controller中获取不为空 但是id 为空
        if(controllerJob != null && (id == null || id < 0)){
            job = controllerJob;
        //如果 controller中获取为空 但是id 不为空
        }else {
            job = jobService.findByIdWithInfo(id);
        }

        if (job == null) {
            return;
        }

        scope.setLocal("job", job);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
