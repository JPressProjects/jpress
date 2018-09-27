package org.springframework.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 在tomcat环境下，由于servlet 3.0 会去自动加载其他jar包的 web-fragment.xml
 * <p>
 * 而dubbo下的 web-fragment.xml 又去加载 org.springframework.web.context.ContextLoaderListener
 * 从而造成tomcat找不到 ContextLoaderListener ， 无法启动的情况
 * <p>
 * 解决方案有2：
 * 1、排除dubbo，但是以后若是用到 dubbo 还是会出现这样的问题
 * 2、写此类，做一个空实现。
 * <p>
 * 另：
 * 已经向dubbo官方提出issue 并 给出解决方案：https://github.com/apache/incubator-dubbo/issues/2570
 * 等待 dubbo 官方解决，解决之后会删除此类。
 */
public class ContextLoaderListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}

