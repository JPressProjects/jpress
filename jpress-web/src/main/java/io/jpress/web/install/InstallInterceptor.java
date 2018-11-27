package io.jpress.web.install;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import io.jpress.core.install.JPressInstaller;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.web.install
 */
public class InstallInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {

        if (JPressInstaller.isInstalled()) {
            inv.getController().renderError(404);
            return;
        }

        inv.invoke();
    }
}
