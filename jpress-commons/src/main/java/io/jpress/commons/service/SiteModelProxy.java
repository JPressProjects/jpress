/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.commons.service;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.CPI;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.db.model.JbootModel;
import io.jboot.utils.ClassUtil;
import io.jpress.SiteContext;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Method;

public class SiteModelProxy {

    private static ThreadLocal<long[]> hintSiteIds = new ThreadLocal<>();

    /**
     * 忽略站点条件，查询所有站点
     */
    public static void useAllSites() {
        hintSiteIds.set(new long[]{-1L});
    }

    /**
     * 查找主站点
     */
    public static void useMainSite() {
        hintSiteIds.set(new long[]{0L});
    }


    /**
     * 查询指定站点
     *
     * @param siteIds
     */
    public static void useSites(long[] siteIds) {
        hintSiteIds.set(siteIds);
    }

    /**
     * 清除站点查询
     */
    public static void clearUsed() {
        hintSiteIds.remove();
    }


    static <T> T get(Class<T> target) {
        javassist.util.proxy.ProxyFactory factory = new javassist.util.proxy.ProxyFactory();
        factory.setSuperclass(target);
        final Class<?> proxyClass = factory.createClass();


        T proxyObject = null;
        try {
            proxyObject = (T) proxyClass.newInstance();
            ((ProxyObject) proxyObject).setHandler(new ProcessColumnsHandler());
        } catch (Throwable e) {
            LogKit.error(e.toString(), e);
        }

        return proxyObject;
    }


    static class ProcessColumnsHandler implements MethodHandler {

        private static final String processColumns = "processColumns";
        private static final String copy = "copy";

        @Override
        public Object invoke(Object self, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable {

            if (processColumns.equals(originalMethod.getName())) {
                Columns columns = (Columns) args[0];

                long[] siteIds = hintSiteIds.get();
                if (siteIds != null && siteIds.length > 0) {
                    if (siteIds.length == 1 && siteIds[0] < 0) {
                        //忽略站点条件，查询所有站点
                    } else if (!columns.containsName("site_id")) {
                        columns.addToFirst(Column.create("site_id", siteIds, Column.LOGIC_IN));
                    }
                } else if (!columns.containsName("site_id")) {
                    columns.addToFirst(Column.create("site_id", SiteContext.getSiteId()));
                }
            }

            //copy
            else if (copy.equals(originalMethod.getName())) {
                JbootModel selfModel = (JbootModel) self;
                JbootModel dao = (JbootModel) get(ClassUtil.getUsefulClass(self.getClass()));
                dao.put(CPI.getAttrs(selfModel));
                return dao;
            }

            return proxyMethod.invoke(self, args);
        }

    }


}



