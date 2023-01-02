/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.base;

import com.jfinal.aop.Before;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import io.jboot.web.render.JbootJsonRender;
import io.jpress.JPressConsts;
import io.jpress.web.interceptor.ApiInterceptor;
import io.jpress.web.interceptor.UserInterceptor;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@Before({ApiInterceptor.class, UserInterceptor.class})
public abstract class ApiControllerBase extends ControllerBase {

    public static final int JWT_ERROR_CODE = 401;

    public static final int JWT_LOGICAL_INVALID_CODE = 101;


    protected void renderFailJson(String message) {
        renderJson(Ret.fail("message", message));
    }

    protected void renderFailJson(int code, String message) {
        renderJson(Ret.fail("code", code).set("message", message));
    }

    protected void renderOkJson(String attr, Object value) {
        renderJson(Ret.ok(attr, value));
    }
    protected void renderOkDataJson(Object value){
        renderJson(Ret.ok("data", value));
    }


    /**
     * 获取当前微信用户的 OpenId
     * @return
     */
    @NotAction
    public String getCurrentWechatOpenId() {
        return getJwtPara(JPressConsts.JWT_OPENID);
    }

    /**
     * 获取当前用户的 unionId
     * @return
     */
    @NotAction
    public String getCurrentWechatUnionId() {
        return getJwtPara(JPressConsts.JWT_UNIONID,false);
    }

    /**
     * 获取当前用户的 unionId
     * @return
     */
    @NotAction
    public String getCurrentWechatUnionId(boolean validateNullValue) {
        return getJwtPara(JPressConsts.JWT_UNIONID,validateNullValue);
    }

    @Override
    @NotAction
    public <T> T getJwtPara(String name) {
        return getJwtPara(name, true);
    }


    @NotAction
    public <T> T getJwtPara(String name, boolean validateNullValue) {
        T object = super.getJwtPara(name);
        if (object == null && validateNullValue) {
            renderRelogin();
        }
        return object;
    }

    /**
     * 需要前端重新登录
     */
    @NotAction
    public void renderRelogin() {
        renderError(200, new JbootJsonRender(Ret.ok().set("code", JWT_ERROR_CODE)));
    }


    /**
     * 用于在某些情况，用户已经被删除了，但是前端 JWT 未过期的情况
     */
    @NotAction
    public void renderJwtLogicalInvalid() {
        renderError(200, new JbootJsonRender(Ret.ok().set("code", JWT_LOGICAL_INVALID_CODE)));
    }


}
