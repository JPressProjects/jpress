package io.jpress.module.job.controller.front;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @version V5.0
 * @Title: Job 的 详情页面 Controller，主要是用过 {{@link io.jpress.module.job.directive.JobDirective 来渲染的}}
 */
@RequestMapping("/job")
public class JobDetailController extends TemplateControllerBase {

    @Inject
    private JobService jobService;

    public void index(){

        Long id = getParaToLong();

        if(id == null || id < 0){
            renderError(404);
            return;
        }

        Job job = jobService.findByIdWithInfo(id);

        if(job != null){
            setAttr("job",job);
        }

        render("jobdetail.html");
    }
}
