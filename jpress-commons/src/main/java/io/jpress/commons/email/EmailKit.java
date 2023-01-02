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
package io.jpress.commons.email;

import io.jboot.Jboot;
import io.jboot.utils.NamedThreadPools;
import io.jpress.commons.utils.CommonsUtils;

import java.util.concurrent.ExecutorService;


public class EmailKit {

    private static final String CACHE_NAME = "email_code";

    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3,"user_reset_password");


    /**
     * 发送邮箱验证码
     * @param emailAddr
     * @param code
     * @param email
     * @return
     */
    public static boolean sendEmailCode(String emailAddr, String code, Email email) {

        SimpleEmailSender emailSender = new SimpleEmailSender();

        fixedThreadPool.execute(() -> {
            emailSender.send(email);
        });
        //有效期，2个小时
        Jboot.getCache().put(CACHE_NAME, emailAddr, code, 60 * 60 * 2);
        return true;
    }

    /**
     * 发送重置密码的链接到邮箱
     * @param email
     * @return
     */
    public static boolean sendResetPwdLinkToEmail(Email email) {

        SimpleEmailSender emailSender = new SimpleEmailSender();

        fixedThreadPool.execute(() -> {
            emailSender.send(email);
        });
        return true;
    }

    /**
     * 验证用户输入的验证码是否正确
     *
     * @param email
     * @param code
     * @return
     */
    public static boolean validateCode(String email, String code) {
        String cacheCode = Jboot.getCache().get(CACHE_NAME, email);
        return cacheCode != null && cacheCode.equals(code);
    }


    /**
     * 生成一个四位数字的码
     *
     * @return
     */
    public static String generateCode() {
        return CommonsUtils.generateCode();
    }
}
