///**
// * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
// * <p>
// * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.springframework.web.context;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//
///**
// * 在tomcat环境下，由于servlet 3.0 会去自动加载依赖jar包的 web-fragment.xml
// * <p>
// * 而dubbo.jar下的 web-fragment.xml 又去加载 org.springframework.web.context.ContextLoaderListener
// * 但是，JFinal 和 Jboot 体系是不需要spring的，从而造成tomcat找不到 ContextLoaderListener，而无法启动的情况
// * <p>
// * 解决方案有2：
// * 1、排除dubbo，但是以后若是用到 dubbo 还是肯定还会出现这样的问题
// * 2、写此类，做一个空实现。
// * <p>
// * 另：
// * 此问题已经向dubbo官方提出issue 并 给出解决方案：https://github.com/apache/incubator-dubbo/issues/2570
// * 等待 dubbo 官方解决，解决之后会删除此类。
// */
//public class ContextLoaderListener implements ServletContextListener {
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//
//    }
//}
//
