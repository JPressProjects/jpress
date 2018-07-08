package io.jpress.core.web.base;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.utils.RequestUtils;
import io.jpress.model.Utm;
import io.jpress.service.UtmService;

import javax.inject.Inject;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 用户源拦截器
 * @Package io.jpress.web
 */
public class UTMInterceptor implements Interceptor {

    @Inject
    private UtmService utmService;


    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();

        Utm utm = new Utm();
        utm.setActionKey(inv.getActionKey());
        utm.setActionQuery(controller.getRequest().getQueryString());
        utm.setIp(RequestUtils.getIpAddress(controller.getRequest()));
        utm.setAgent(RequestUtils.getUserAgent(controller.getRequest()));

        utmService.save(utm);
        inv.invoke();

    }
}
