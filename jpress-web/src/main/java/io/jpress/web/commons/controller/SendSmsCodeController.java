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
package io.jpress.web.commons.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.sms.SmsKit;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/commons/getsmscode")
public class SendSmsCodeController extends Controller {


    public void index() {
        if (!validateCaptcha("captcha")) {
            renderJson(Ret.fail().set("message", "验证码错误，请重新输入"));
            return;
        }

        String phone = getPara("phone");
        if (StrUtil.isBlank(phone)) {
            renderJson(Ret.fail().set("message", "手机号不能为空..."));
            return;
        }

        if (!StrUtil.isMobileNumber(phone)) {
            renderJson(Ret.fail().set("message", "你输入的手机号码不正确"));
            return;
        }


        String code = SmsKit.generateCode();
        String template = JPressOptions.get("reg_sms_validate_template");
        String sign = JPressOptions.get("reg_sms_validate_sign");

        boolean sendOk = SmsKit.sendCode(phone, code, template, sign);

        if (sendOk) {
            renderJson(Ret.ok().set("message", "短信发送成功，请手机查看"));
        } else {
            renderJson(Ret.fail().set("message", "短信实发失败，请联系管理员"));
        }

    }


}
