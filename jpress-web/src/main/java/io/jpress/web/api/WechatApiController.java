/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.api;

import com.jfinal.aop.Inject;
import com.jfinal.wxaapp.api.WxaUserApi;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.service.UserService;
import io.jpress.web.base.ApiControllerBase;


/**
 * 微信公众号相关的API
 */
@RequestMapping("/api/wechat")
public class WechatApiController extends ApiControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private WxaUserApi wxaUserApi;

}
