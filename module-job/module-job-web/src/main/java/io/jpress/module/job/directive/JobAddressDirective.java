package io.jpress.module.job.directive;


import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.job.model.JobAddress;
import io.jpress.module.job.service.JobAddressService;

import java.util.List;

/**
 * @version V5.0
 * @description: 所有地址
 */
@JFinalDirective("jobAddress")
public class JobAddressDirective extends JbootDirectiveBase {


    @Inject
    private JobAddressService jobAddressService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        List<JobAddress> addressList = jobAddressService.findAll();

        if (addressList == null) {
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
