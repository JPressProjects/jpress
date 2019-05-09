/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.interceptor;

import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jpress.JPressConsts;
import io.jpress.model.Utm;
import io.jpress.service.UtmService;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: utm拦截器
 * @Package io.jpress.web
 */
public class UTMInterceptor implements Interceptor {

    @Inject
    private UtmService utmService;


    @Override
    public void intercept(Invocation inv) {
        try {
            doRecordUTM(inv);
        } finally {
            inv.invoke();
        }
    }

    private void doRecordUTM(Invocation inv) {
        Controller ctr = inv.getController();

        Utm utm = new Utm();
        utm.setId(StrUtil.uuid());
        utm.setActionKey(ctr.getRequest().getRequestURI());
        utm.setActionQuery(ctr.getRequest().getQueryString());
        utm.setIp(RequestUtil.getIpAddress(ctr.getRequest()));
        utm.setAgent(RequestUtil.getUserAgent(ctr.getRequest()));
        utm.setReferer(RequestUtil.getReferer(ctr.getRequest()));


        String uid = CookieUtil.get(ctr, JPressConsts.COOKIE_UID);
        if (StrUtil.isNotBlank(uid)) {
            utm.setUserId(Long.valueOf(uid));
        }

        /**
         * 可能是API的用户，API 通过 jwt 获取用户信息
         */
        else if (ctr instanceof JbootController) {
            JbootController c = (JbootController) ctr;
            Long userId = c.getJwtAttr(JPressConsts.JWT_USERID);
            if (userId != null) utm.setUserId(userId);
        }

        /**
         * 当用户未登录的情况下，创建匿名记录
         */
        else {
            //anonym
            String anonym = CookieUtil.get(ctr, JPressConsts.COOKIE_ANONYM);
            if (StrUtil.isNotBlank(anonym)) {
                utm.setAnonym(anonym);
            } else {
                CookieUtil.put(ctr, JPressConsts.COOKIE_ANONYM, StrUtil.uuid(), 60 * 60 * 24 * 365);
            }
        }

        utm.setSource(ctr.getPara("source"));
        utm.setMedium(ctr.getPara("medium"));
        utm.setCampaign(ctr.getPara("campaign"));
        utm.setContent(ctr.getPara("content"));
        utm.setTerm(ctr.getPara("term"));

        utmService.doRecord(utm);
    }
}
