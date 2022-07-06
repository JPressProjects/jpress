package io.jpress.module.job.controller.front;

import com.jfinal.aop.Inject;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.module.job.model.Job;
import io.jpress.module.job.service.JobService;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @version V5.0
 * @Title: Job 的 Controller，主要是用过 {{@link io.jpress.module.job.directive.JobPageDirective 来渲染的}}
 */
@RequestMapping("/job/list")
public class JobListController extends TemplateControllerBase {

    @Inject
    private JobService jobService;

    public void index() {


        render("joblist.html");
    }



    private void setSeoInfos(Job job) {
        setSeoTitle(job.getTitle());
        setSeoKeywords(job.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(job.getMetaDescription()) ? CommonsUtils.maxLength(job.getContent(), 100) : job.getContent());
    }






}
