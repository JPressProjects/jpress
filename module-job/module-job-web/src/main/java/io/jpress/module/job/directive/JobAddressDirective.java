package io.jpress.module.job.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.JobCategory;
import io.jpress.module.job.service.JobCategoryService;

import java.util.List;

/**
 * @version V5.0
 * @description: 所有地址
 */
@JFinalDirective("jobAddress")
public class JobAddressDirective extends JbootDirectiveBase {


    @Inject
    private JobCategoryService jobCategoryService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        List<JobCategory> addressList = jobCategoryService.findListByColumns(Columns.create().eq("type", JobCategory.CATEGORY_TYPE_ADDRESS));

        if (addressList == null || addressList.isEmpty()) {
            return;
        }

        scope.setLocal("addressList", addressList);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
