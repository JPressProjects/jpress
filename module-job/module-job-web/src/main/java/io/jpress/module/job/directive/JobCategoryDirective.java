package io.jpress.module.job.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobCategoryService;

import java.util.List;

/**
 * @description: 所有岗位分类
 * @version V5.0
 */
@JFinalDirective("jobCategories")
public class JobCategoryDirective extends JbootDirectiveBase {


    @Inject
    private JobCategoryService jobCategoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        List<JobCategory> categoryList = jobCategoryService.findAll();

        if(categoryList == null){
            return;
        }

        scope.setLocal("categoryList",categoryList);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
